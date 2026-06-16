import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CrmApiService } from '../shared/services/crm-api.service';
import { ReportType, TYPE_LABELS } from '../shared/models/report.model';

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
  reportTypes = Object.keys(TYPE_LABELS) as ReportType[];
  loading = false;
  error = '';
  success = '';

  form = this.fb.group({
    title: ['', Validators.required],
    reference: [`RAP-${Date.now().toString().slice(-6)}`, Validators.required],
    reportType: ['ETUDE_GEOTECHNIQUE' as ReportType, Validators.required],
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
      revenueAmount: v.revenueAmount ?? 0
    }).subscribe({
      next: () => {
        this.success = 'Rapport cree avec succes.';
        setTimeout(() => this.router.navigate(['/kanban']), 800);
      },
      error: () => {
        this.error = 'Impossible de creer le rapport.';
        this.loading = false;
      }
    });
  }
}
