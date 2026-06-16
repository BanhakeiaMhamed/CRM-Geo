import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../shared/Authentication/auth.service';
import { CrmApiService } from '../shared/services/crm-api.service';
import { DashboardOverview, UserProfile } from '../shared/models/report.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly api = inject(CrmApiService);

  profile: UserProfile | null = null;
  overview: DashboardOverview | null = null;
  loading = true;
  error = '';

  ngOnInit(): void {
    this.api.getProfile().subscribe({
      next: profile => {
        this.profile = profile;
        this.api.getDashboardOverview().subscribe({
          next: overview => {
            this.overview = overview;
            this.loading = false;
          },
          error: () => {
            this.error = 'Impossible de charger le tableau de bord.';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.error = 'Impossible de charger votre profil. Verifiez que le backend est demarre.';
        this.loading = false;
      }
    });
  }

  async logout(): Promise<void> {
    await this.authService.signOut();
    window.location.href = '/login';
  }
}
