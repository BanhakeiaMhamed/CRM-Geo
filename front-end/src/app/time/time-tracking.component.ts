import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CrmApiService } from '../shared/services/crm-api.service';
import { Report, TimeEntry } from '../shared/models/report.model';

@Component({
  selector: 'app-time-tracking',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './time-tracking.component.html',
  styleUrl: './time-tracking.component.css'
})
export class TimeTrackingComponent implements OnInit {
  private readonly api = inject(CrmApiService);
  private readonly fb = inject(FormBuilder);

  reports: Report[] = [];
  entries: TimeEntry[] = [];
  loading = true;
  saving = false;
  error = '';

  form = this.fb.group({
    reportReference: ['', Validators.required],
    entryDate: [new Date().toISOString().slice(0, 10), Validators.required],
    hoursSpent: [1, [Validators.required, Validators.min(0.25)]],
    comment: ['']
  });

  ngOnInit(): void {
    this.api.getMyReports().subscribe({
      next: reports => {
        this.reports = reports;
        this.loadEntries();
      },
      error: () => {
        this.error = 'Impossible de charger les rapports.';
        this.loading = false;
      }
    });
  }

  loadEntries(): void {
    this.api.getTimeEntries().subscribe({
      next: entries => {
        this.entries = entries.sort((a, b) => b.entryDate.localeCompare(a.entryDate));
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le suivi du temps.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const v = this.form.getRawValue();
    this.api.logTime({
      reportReference: v.reportReference!,
      entryDate: v.entryDate!,
      hoursSpent: v.hoursSpent!,
      comment: v.comment || undefined
    }).subscribe({
      next: () => {
        this.saving = false;
        this.form.patchValue({ hoursSpent: 1, comment: '' });
        this.loadEntries();
      },
      error: () => {
        this.saving = false;
        this.error = 'Impossible d enregistrer le temps.';
      }
    });
  }
}
