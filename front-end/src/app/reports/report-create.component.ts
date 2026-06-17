import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CrmApiService } from '../shared/services/crm-api.service';
import { ReportType, REPORT_TYPE_GROUPS, TYPE_LABELS } from '../shared/models/report.model';

@Component({
  selector: 'app-report-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './report-create.component.html',
  styleUrl: './report-create.component.css'
})
export class ReportCreateComponent {
  private readonly api = inject(CrmApiService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  typeLabels = TYPE_LABELS;
  typeGroups = REPORT_TYPE_GROUPS;
  loading = false;
  error = '';
  success = '';
  selectedPdf: File | null = null;

  onPdfChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedPdf = input.files?.[0] ?? null;
  }

  form = this.fb.group({
    title: ['', Validators.required],
    reference: [`RAP-${Date.now().toString().slice(-6)}`, Validators.required],
    reportType: ['G1_ES' as ReportType, Validators.required],
    address: ['', Validators.required],
    postalCode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]],
    city: ['', Validators.required],
    revenueAmount: [0, [Validators.min(0)]]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';
    const v = this.form.getRawValue();
    this.api.createReport({
      title: v.title!,
      reference: v.reference!,
      reportType: v.reportType!,
      address: v.address!,
      postalCode: v.postalCode!,
      city: v.city!,
      revenueAmount: v.revenueAmount ?? 0
    }).subscribe({
      next: (report) => {
        if (this.selectedPdf) {
          this.api.uploadReportPdf(report.reference, this.selectedPdf).subscribe({
            next: () => this.finishCreation('Rapport cree avec PDF joint.'),
            error: () => this.finishCreation('Rapport cree, mais le PDF n\'a pas pu etre joint. Ajoutez-le depuis le Kanban.')
          });
        } else {
          this.finishCreation('Rapport cree avec succes.');
        }
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.error = 'Serveur inaccessible. Verifiez que le backend est demarre (port 8081).';
        } else {
          this.error = err.error?.message ?? 'Impossible de creer le rapport.';
        }
        this.loading = false;
      }
    });
  }

  private finishCreation(message: string): void {
    this.success = message;
    setTimeout(() => this.router.navigate(['/kanban']), 800);
  }
}
