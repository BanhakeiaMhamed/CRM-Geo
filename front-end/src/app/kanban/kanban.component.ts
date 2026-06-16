import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  CdkDragDrop,
  DragDropModule,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import { CrmApiService } from '../shared/services/crm-api.service';
import {
  ALLOWED_TRANSITIONS,
  COLUMN_COLORS,
  KANBAN_COLUMNS,
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
  statusLabels = STATUS_LABELS;
  typeLabels = TYPE_LABELS;
  columnColors = COLUMN_COLORS;
  columnLists: Record<ReportStatus, Report[]> = this.emptyColumnLists();
  loading = true;
  error = '';
  actionLoading: string | null = null;
  draggingFrom: ReportStatus | null = null;
  dropHint = '';

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

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    this.transition(report, targetStatus, true);
  }

  submitForValidation(report: Report): void {
    this.transition(report, 'EN_VALIDATION');
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
      error: () => {
        this.actionLoading = null;
        alert('Transition impossible pour ce rapport.');
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
