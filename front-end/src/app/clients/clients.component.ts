import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CrmApiService } from '../shared/services/crm-api.service';
import { Client } from '../shared/models/report.model';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.css'
})
export class ClientsComponent implements OnInit {
  private readonly api = inject(CrmApiService);
  private readonly fb = inject(FormBuilder);

  clients: Client[] = [];
  loading = true;
  saving = false;
  error = '';
  success = '';
  clientKind: 'PARTICULIER' | 'PROFESSIONAL' = 'PARTICULIER';

  particulierForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['']
  });

  profesionnelForm = this.fb.group({
    companyName: ['', Validators.required],
    siren: ['', Validators.required],
    siret: ['', Validators.required],
    companyType: ['SARL', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['']
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getClients().subscribe({
      next: clients => {
        this.clients = clients;
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les clients.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    this.error = '';
    this.success = '';
    if (this.clientKind === 'PARTICULIER') {
      if (this.particulierForm.invalid) {
        this.particulierForm.markAllAsTouched();
        return;
      }
      this.saving = true;
      const v = this.particulierForm.getRawValue();
      this.api.createParticulier({
        firstName: v.firstName!,
        lastName: v.lastName!,
        email: v.email!,
        phone: v.phone || undefined
      }).subscribe({
        next: () => {
          this.saving = false;
          this.success = 'Client particulier cree.';
          this.particulierForm.reset();
          this.load();
        },
        error: () => {
          this.saving = false;
          this.error = 'Creation impossible.';
        }
      });
      return;
    }
    if (this.profesionnelForm.invalid) {
      this.profesionnelForm.markAllAsTouched();
      return;
    }
    this.saving = true;
    const v = this.profesionnelForm.getRawValue();
    this.api.createProfesionnel({
      companyName: v.companyName!,
      siren: v.siren!,
      siret: v.siret!,
      companyType: v.companyType!,
      email: v.email!,
      phone: v.phone || undefined
    }).subscribe({
      next: () => {
        this.saving = false;
        this.success = 'Client professionnel cree.';
        this.profesionnelForm.reset({ companyType: 'SARL' });
        this.load();
      },
      error: () => {
        this.saving = false;
        this.error = 'Creation impossible.';
      }
    });
  }

  remove(client: Client): void {
    if (!confirm('Supprimer ' + client.displayName + ' ?')) return;
    this.api.deleteClient(client.id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Suppression impossible.'
    });
  }
}
