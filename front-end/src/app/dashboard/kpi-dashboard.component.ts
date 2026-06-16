import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { CrmApiService } from '../shared/services/crm-api.service';
import {
  DashboardBenefits,
  DashboardOverview,
  ENGINEER_CHART_COLORS,
  EngineerBenefit,
  KPI_STATUS_COLORS,
  KPI_STATUS_KEYS,
  PeriodFilter,
  ReportStatus,
  STATUS_LABELS
} from '../shared/models/report.model';

@Component({
  selector: 'app-kpi-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './kpi-dashboard.component.html',
  styleUrl: './kpi-dashboard.component.css'
})
export class KpiDashboardComponent implements OnInit {
  private readonly api = inject(CrmApiService);

  overview: DashboardOverview | null = null;
  benefits: DashboardBenefits | null = null;
  statusLabels = STATUS_LABELS;
  statusKeys = KPI_STATUS_KEYS;
  statusColors = KPI_STATUS_COLORS;
  engineerColors = ENGINEER_CHART_COLORS;
  months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  monthLabels = ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aout', 'Sep', 'Oct', 'Nov', 'Dec'];
  currentYear = new Date().getFullYear();
  loading = true;
  error = '';
  selectedEngineer: EngineerBenefit | null = null;
  selectedEngineerIndex = 0;
  periodFilter: PeriodFilter = 'ANNEE';
  periodNumber = new Date().getMonth() + 1;

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    forkJoin({
      overview: this.api.getDashboardOverview(),
      benefits: this.api.getDashboardBenefits(
        this.currentYear,
        this.periodFilter,
        this.periodFilter === 'ANNEE' ? undefined : this.periodNumber
      )
    }).subscribe({
      next: ({ overview, benefits }) => {
        this.overview = overview;
        this.benefits = benefits;
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le dashboard KPI.';
        this.loading = false;
      }
    });
  }

  onPeriodChange(): void {
    this.selectedEngineer = null;
    this.loadDashboard();
  }

  periodLabel(): string {
    if (this.periodFilter === 'MENSUEL') {
      return this.monthLabels[this.periodNumber - 1] + ' ' + this.currentYear;
    }
    if (this.periodFilter === 'TRIMESTRIEL') {
      return 'T' + this.periodNumber + ' ' + this.currentYear;
    }
    return 'Annee ' + this.currentYear;
  }

  toggleEngineerCard(engineer: EngineerBenefit, index = 0): void {
    if (this.selectedEngineer?.email === engineer.email) {
      this.selectedEngineer = null;
    } else {
      this.selectedEngineer = engineer;
      this.selectedEngineerIndex = index;
    }
  }

  closeEngineerCard(): void {
    this.selectedEngineer = null;
  }

  isCardVisible(engineer: EngineerBenefit): boolean {
    return this.selectedEngineer?.email === engineer.email;
  }

  count(status: ReportStatus): number {
    return this.overview?.reportsByStatus[status] ?? 0;
  }

  statusLabel(status: ReportStatus): string {
    return this.statusLabels[status];
  }

  statusTotal(): number {
    return this.statusKeys.reduce((sum, status) => sum + this.count(status), 0);
  }

  barWidth(status: ReportStatus): string {
    const total = this.statusTotal();
    if (total === 0) return '0%';
    return `${(this.count(status) / total) * 100}%`;
  }

  maxEngineerRevenue(): number {
    const values = this.benefits?.engineers.map(e => e.totalRevenue) ?? [0];
    return Math.max(...values, 1);
  }

  engineerBarWidth(revenue: number): string {
    return `${(revenue / this.maxEngineerRevenue()) * 100}%`;
  }

  engineerName(engineer: EngineerBenefit): string {
    return `${engineer.firstName} ${engineer.lastName}`;
  }

  formatDate(isoDate: string): string {
    const [year, month, day] = isoDate.split('-');
    return `${day}/${month}/${year}`;
  }

  monthRevenue(engineer: EngineerBenefit, month: number): number {
    return engineer.monthlyRevenue.find(m => m.month === month)?.revenue ?? 0;
  }

  monthTotal(month: number): number {
    return this.benefits?.engineers.reduce((sum, e) => sum + this.monthRevenue(e, month), 0) ?? 0;
  }

  maxMonthTotal(): number {
    const values = this.months.map(m => this.monthTotal(m));
    return Math.max(...values, 1);
  }

  monthStackHeight(month: number): string {
    return `${(this.monthTotal(month) / this.maxMonthTotal()) * 100}%`;
  }

  segmentHeight(engineer: EngineerBenefit, month: number): string {
    const total = this.monthTotal(month);
    if (total === 0) return '0%';
    return `${(this.monthRevenue(engineer, month) / total) * 100}%`;
  }
}
