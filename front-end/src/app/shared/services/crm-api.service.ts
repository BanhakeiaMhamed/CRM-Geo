import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Client,
  DashboardBenefits,
  DashboardOverview,
  EngineerSummary,
  InterventionMapItem,
  PeriodFilter,
  Report,
  ReportStatus,
  ReportType,
  TimeEntry,
  UserProfile
} from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class CrmApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/api/me`);
  }

  getKanban(): Observable<Partial<Record<ReportStatus, Report[]>>> {
    return this.http.get<Partial<Record<ReportStatus, Report[]>>>(`${this.baseUrl}/api/reports/kanban`);
  }

  getMyReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.baseUrl}/api/reports`);
  }

  getDashboardOverview(): Observable<DashboardOverview> {
    return this.http.get<DashboardOverview>(`${this.baseUrl}/api/dashboard/overview`);
  }

  getDashboardBenefits(
    year = new Date().getFullYear(),
    periodFilter: PeriodFilter = 'ANNEE',
    periodNumber?: number
  ): Observable<DashboardBenefits> {
    let params = new HttpParams().set('year', year);
    if (periodFilter === 'MENSUEL' && periodNumber) {
      params = params.set('periodType', 'MENSUEL').set('periodNumber', periodNumber);
    } else if (periodFilter === 'TRIMESTRIEL' && periodNumber) {
      params = params.set('periodType', 'TRIMESTRIEL').set('periodNumber', periodNumber);
    }
    return this.http.get<DashboardBenefits>(`${this.baseUrl}/api/dashboard/benefits`, { params });
  }

  transitionReport(reference: string, targetStatus: ReportStatus): Observable<Report> {
    return this.http.patch<Report>(`${this.baseUrl}/api/reports/${reference}/transition`, { targetStatus });
  }

  createReport(data: {
    title: string;
    reference: string;
    reportType: ReportType;
    revenueAmount?: number;
    address: string;
    postalCode: string;
    city: string;
  }): Observable<Report> {
    return this.http.post<Report>(`${this.baseUrl}/api/reports`, data);
  }

  updatePageCount(reference: string, pageCount: number): Observable<Report> {
    return this.http.patch<Report>(`${this.baseUrl}/api/reports/${reference}/pages`, { pageCount });
  }

  getInterventionsMap(): Observable<InterventionMapItem[]> {
    return this.http.get<InterventionMapItem[]>(`${this.baseUrl}/api/interventions/map`);
  }

  uploadReportPdf(reference: string, file: File): Observable<Report> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Report>(`${this.baseUrl}/api/reports/${reference}/pdf`, formData);
  }

  downloadReportPdf(reference: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/api/reports/${reference}/pdf`, { responseType: 'blob' });
  }

  getTimeEntries(): Observable<TimeEntry[]> {
    return this.http.get<TimeEntry[]>(`${this.baseUrl}/api/time-entries`);
  }

  logTime(data: {
    reportReference: string;
    entryDate: string;
    hoursSpent: number;
    comment?: string;
  }): Observable<TimeEntry> {
    return this.http.post<TimeEntry>(`${this.baseUrl}/api/time-entries`, data);
  }

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.baseUrl}/api/clients`);
  }

  createParticulier(data: {
    firstName: string;
    lastName: string;
    email: string;
    phone?: string;
  }): Observable<Client> {
    return this.http.post<Client>(`${this.baseUrl}/api/clients/particulier`, data);
  }

  createProfesionnel(data: {
    companyName: string;
    siren: string;
    siret: string;
    companyType: string;
    email: string;
    phone?: string;
  }): Observable<Client> {
    return this.http.post<Client>(`${this.baseUrl}/api/clients/profesionnel`, data);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/clients/${id}`);
  }

  getEngineers(): Observable<EngineerSummary[]> {
    return this.http.get<EngineerSummary[]>(`${this.baseUrl}/api/engineers`);
  }

  attachEngineer(data: { firstName: string; lastName: string; email: string }): Observable<EngineerSummary> {
    return this.http.post<EngineerSummary>(`${this.baseUrl}/api/engineers`, data);
  }
}
