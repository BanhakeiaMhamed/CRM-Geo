import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CrmApiService } from '../shared/services/crm-api.service';
import { EngineerSummary } from '../shared/models/report.model';

@Component({
  selector: 'app-engineers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './engineers.component.html',
  styleUrl: './engineers.component.css'
})
export class EngineersComponent implements OnInit {
  private readonly api = inject(CrmApiService);
  private readonly fb = inject(FormBuilder);

  engineers: EngineerSummary[] = [];
  loading = true;
  saving = false;
  error = '';
  success = '';

  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.getEngineers().subscribe({
      next: engineers => {
        this.engineers = engineers;
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les ingenieurs.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.error = '';
    const v = this.form.getRawValue();
    this.api.attachEngineer({
      firstName: v.firstName!,
      lastName: v.lastName!,
      email: v.email!
    }).subscribe({
      next: () => {
        this.saving = false;
        this.success = 'Ingenieur rattache.';
        this.form.reset();
        this.load();
      },
      error: () => {
        this.saving = false;
        this.error = 'Rattachement impossible (email deja utilise ?).';
      }
    });
  }
}
