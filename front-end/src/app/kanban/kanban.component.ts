import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  CdkDragDrop,
  DragDropModule,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import { CrmApiService } from '../shared/services/crm-api.service';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ALLOWED_TRANSITIONS,
  COLUMN_COLORS,
  KANBAN_COLUMNS,
  MIN_PAGES_FOR_VALIDATION,
  Report,
  ReportStatus,
  STATUS_LABELS,
  TYPE_LABELS
} from '../shared/models/report.model';

@Component({
  selector: 'app-kanban',
  standalone: true,
  imports: [CommonModule, RouterLink, DragDropModule],
  templateUrl: './kanban.component.html',
  styleUrl: './kanban.component.css'
})
export class KanbanComponent implements OnInit {
  private readonly api = inject(CrmApiService);

  columns = KANBAN_COLUMNS;
  minPages = MIN_PAGES_FOR_VALIDATION;
  statusLabels = STATUS_LABELS;
  typeLabels = TYPE_LABELS;
  columnColors = COLUMN_COLORS;
  columnLists: Record<ReportStatus, Report[]> = this.emptyColumnLists();
  loading = true;
  error = '';
  actionLoading: string | null = null;
  draggingFrom: ReportStatus | null = null;
  dropHint = '';
  /** Brouillon local du nombre de pages (tant que OK n'est pas clique). */
  pageDrafts: Record<string, number> = {};

  ngOnInit(): void {
    this.loadBoard();
  }

  loadBoard(): void {
    this.loading = true;
    this.api.getKanban().subscribe({
      next: board => {
        this.columnLists = this.emptyColumnLists();
        for (const col of this.columns) {
          this.columnLists[col] = [...(board[col] ?? [])];
        }
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le tableau Kanban.';
        this.loading = false;
      }
    });
  }

  onDragStarted(status: ReportStatus): void {
    this.draggingFrom = status;
    const targets = ALLOWED_TRANSITIONS[status] ?? [];
    this.dropHint = targets.length
      ? `Deposez dans : ${targets.map(t => this.statusLabels[t]).join(' ou ')}`
      : '';
  }

  onDragEnded(): void {
    this.draggingFrom = null;
    this.dropHint = '';
  }

  isValidDropTarget(target: ReportStatus): boolean {
    if (!this.draggingFrom || this.draggingFrom === target) {
      return false;
    }
    return (ALLOWED_TRANSITIONS[this.draggingFrom] ?? []).includes(target);
  }

  canDragFrom(status: ReportStatus): boolean {
    return (ALLOWED_TRANSITIONS[status]?.length ?? 0) > 0;
  }

  onDrop(event: CdkDragDrop<Report[]>): void {
    if (event.previousContainer === event.container) {
      return;
    }

    const sourceStatus = event.previousContainer.id as ReportStatus;
    const targetStatus = event.container.id as ReportStatus;
    const report = event.previousContainer.data[event.previousIndex];

    if (!this.isTransitionAllowed(sourceStatus, targetStatus)) {
      alert(`Transition impossible vers « ${this.statusLabels[targetStatus]} ».`);
      return;
    }

    if (targetStatus === 'EN_VALIDATION' && !this.canSubmitForValidation(report)) {
      alert(this.validationBlockReason(report));
      return;
    }

    if (targetStatus === 'EN_VALIDATION') {
      this.submitForValidation(report);
      return;
    }

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    this.transition(report, targetStatus, true);
  }

  submitForValidation(report: Report): void {
    if (!this.canSubmitForValidation(report)) {
      alert(this.validationBlockReason(report));
      return;
    }
    const pageCount = this.getPageCount(report);
    if (pageCount !== (report.pageCount ?? 0)) {
      this.actionLoading = report.reference;
      this.api.updatePageCount(report.reference, pageCount).subscribe({
        next: updated => {
          report.pageCount = updated.pageCount;
          delete this.pageDrafts[report.reference];
          this.transition(report, 'EN_VALIDATION');
        },
        error: () => {
          this.actionLoading = null;
          alert('Impossible de mettre a jour le nombre de pages.');
        }
      });
      return;
    }
    this.transition(report, 'EN_VALIDATION');
  }

  canSubmitForValidation(report: Report): boolean {
    return this.getPageCount(report) >= this.minPages && this.hasPdfAttached(report);
  }

  hasPdfAttached(report: Report): boolean {
    return report.hasPdf === true || !!report.pdfFileName;
  }

  getPageCount(report: Report): number {
    return this.pageDrafts[report.reference] ?? report.pageCount ?? 0;
  }

  onPageInput(report: Report, value: string): void {
    this.pageDrafts[report.reference] = Math.max(0, parseInt(value, 10) || 0);
  }

  validationBlockReason(report: Report): string {
    const missingPages = this.getPageCount(report) < this.minPages;
    const missingPdf = !this.hasPdfAttached(report);
    if (missingPages && missingPdf) {
      return `Minimum ${this.minPages} pages et un PDF requis avant validation.`;
    }
    if (missingPages) {
      return `Minimum ${this.minPages} pages requis (actuel : ${this.getPageCount(report)}). Cliquez OK apres la saisie ou soumettez directement.`;
    }
    return 'Joignez le PDF du rapport avant validation.';
  }

  onPdfSelected(report: Report, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    this.actionLoading = report.reference;
    this.api.uploadReportPdf(report.reference, file).subscribe({
      next: updated => {
        report.hasPdf = updated.hasPdf ?? !!updated.pdfFileName;
        report.pdfFileName = updated.pdfFileName;
        this.actionLoading = null;
        input.value = '';
      },
      error: (err: HttpErrorResponse) => {
        this.actionLoading = null;
        input.value = '';
        alert(err.error?.message ?? 'Impossible de joindre le PDF.');
      }
    });
  }

  openPdf(report: Report): void {
    this.api.downloadReportPdf(report.reference).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => alert('Impossible d\'ouvrir le PDF.')
    });
  }

  canUploadPdf(col: ReportStatus): boolean {
    return col === 'EN_COURS' || col === 'EN_RECTIFICATION';
  }

  savePageCount(report: Report, value: string): void {
    const pageCount = Math.max(0, parseInt(value, 10) || 0);
    this.pageDrafts[report.reference] = pageCount;
    this.actionLoading = report.reference;
    this.api.updatePageCount(report.reference, pageCount).subscribe({
      next: updated => {
        report.pageCount = updated.pageCount;
        delete this.pageDrafts[report.reference];
        this.actionLoading = null;
      },
      error: () => {
        this.actionLoading = null;
        alert('Impossible de mettre a jour le nombre de pages.');
      }
    });
  }

  pageProgress(report: Report): string {
    return `${this.getPageCount(report)}/${this.minPages}`;
  }

  validationReady(report: Report): boolean {
    return this.canSubmitForValidation(report);
  }

  requestRectification(report: Report): void {
    this.transition(report, 'EN_RECTIFICATION');
  }

  validate(report: Report): void {
    this.transition(report, 'VALIDE');
  }

  send(report: Report): void {
    this.transition(report, 'ENVOYE');
  }

  closeReport(report: Report): void {
    this.transition(report, 'CLOTURE');
  }

  resumeAfterRectification(report: Report): void {
    this.transition(report, 'EN_COURS');
  }

  private isTransitionAllowed(from: ReportStatus, to: ReportStatus): boolean {
    return (ALLOWED_TRANSITIONS[from] ?? []).includes(to);
  }

  private transition(report: Report, target: ReportStatus, alreadyMoved = false): void {
    const source = report.status;
    this.actionLoading = report.reference;
    this.api.transitionReport(report.reference, target).subscribe({
      next: () => {
        if (alreadyMoved) {
          report.status = target;
        } else {
          this.moveReportLocally(report, source, target);
        }
        this.actionLoading = null;
      },
      error: (err: HttpErrorResponse) => {
        this.actionLoading = null;
        const msg = err.error?.message ?? 'Transition impossible pour ce rapport.';
        alert(msg);
        this.loadBoard();
      }
    });
  }

  private moveReportLocally(report: Report, source: ReportStatus, target: ReportStatus): void {
    const fromList = this.columnLists[source];
    const idx = fromList.findIndex(r => r.reference === report.reference);
    if (idx >= 0) {
      fromList.splice(idx, 1);
    }
    report.status = target;
    this.columnLists[target].push(report);
  }

  private emptyColumnLists(): Record<ReportStatus, Report[]> {
    return {
      BROUILLON: [],
      EN_COURS: [],
      EN_VALIDATION: [],
      VALIDE: [],
      EN_RECTIFICATION: [],
      ENVOYE: [],
      CLOTURE: []
    };
  }
}
