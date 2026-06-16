export type ReportStatus =
  | 'BROUILLON' | 'EN_COURS' | 'EN_VALIDATION' | 'VALIDE'
  | 'EN_RECTIFICATION' | 'ENVOYE' | 'CLOTURE';

export type ReportType =
  | 'ETUDE_GEOTECHNIQUE' | 'ETUDE_STRUCTURE' | 'DIAGNOSTIC'
  | 'AUDIT_ENERGETIQUE' | 'EXPERTISE' | 'AUTRE';

export interface Report {
  id: number;
  reference: string;
  title: string;
  reportType: ReportType;
  status: ReportStatus;
  revenueAmount: number;
  engineerEmail: string;
  tags: string[];
}

export interface UserProfile {
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  enterpriseName: string;
}

export interface DashboardOverview {
  enterpriseName: string;
  reportsByStatus: Record<ReportStatus, number>;
  totalReports: number;
}

export const KANBAN_COLUMNS: ReportStatus[] = [
  'EN_COURS', 'EN_VALIDATION', 'EN_RECTIFICATION', 'VALIDE', 'ENVOYE', 'CLOTURE'
];

/** Transitions autorisees (alignees sur le backend). */
export const ALLOWED_TRANSITIONS: Partial<Record<ReportStatus, ReportStatus[]>> = {
  EN_COURS: ['EN_VALIDATION'],
  EN_VALIDATION: ['VALIDE', 'EN_RECTIFICATION'],
  EN_RECTIFICATION: ['EN_COURS'],
  VALIDE: ['ENVOYE'],
  ENVOYE: ['CLOTURE']
};

export const KPI_STATUS_KEYS: ReportStatus[] = [
  'EN_COURS', 'EN_VALIDATION', 'VALIDE', 'ENVOYE', 'CLOTURE'
];

export const KPI_STATUS_COLORS: Partial<Record<ReportStatus, string>> = {
  EN_COURS: '#3949ab',
  EN_VALIDATION: '#fb8c00',
  VALIDE: '#43a047',
  ENVOYE: '#00897b',
  CLOTURE: '#546e7a'
};

export interface MonthlyRevenue {
  month: number;
  revenue: number;
}

export interface InvoicedReport {
  reference: string;
  title: string;
  status: ReportStatus;
  revenueAmount: number;
  sentAt: string;
}

export interface EngineerBenefit {
  email: string;
  firstName: string;
  lastName: string;
  totalRevenue: number;
  totalHours: number;
  sentReportsCount: number;
  closedReportsCount: number;
  activeReportsCount: number;
  monthlyRevenue: MonthlyRevenue[];
  invoicedReports: InvoicedReport[];
}

export interface DashboardBenefits {
  year: number;
  engineers: EngineerBenefit[];
}

export const ENGINEER_CHART_COLORS = ['#3949ab', '#00897b', '#fb8c00', '#e53935', '#8e24aa'];

export const COLUMN_COLORS: Partial<Record<ReportStatus, string>> = {
  EN_COURS: '#3949ab',
  EN_VALIDATION: '#fb8c00',
  EN_RECTIFICATION: '#e53935',
  VALIDE: '#43a047',
  ENVOYE: '#00897b',
  CLOTURE: '#546e7a'
};

export const STATUS_LABELS: Record<ReportStatus, string> = {
  BROUILLON: 'Brouillon',
  EN_COURS: 'En cours',
  EN_VALIDATION: 'En validation',
  VALIDE: 'Valide',
  EN_RECTIFICATION: 'Rectification',
  ENVOYE: 'Envoye',
  CLOTURE: 'Cloture'
};

export const TYPE_LABELS: Record<ReportType, string> = {
  ETUDE_GEOTECHNIQUE: 'Etude geotechnique',
  ETUDE_STRUCTURE: 'Etude structure',
  DIAGNOSTIC: 'Diagnostic',
  AUDIT_ENERGETIQUE: 'Audit energetique',
  EXPERTISE: 'Expertise',
  AUTRE: 'Autre'
};

export type ClientType = 'PARTICULIER' | 'PROFESSIONAL';

export interface Client {
  id: number;
  clientType: ClientType;
  email: string;
  phone?: string;
  displayName: string;
  firstName?: string;
  lastName?: string;
  companyName?: string;
  siret?: string;
  engineerEmail?: string;
}

export interface EngineerSummary {
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface TimeEntry {
  id: number;
  reportReference: string;
  reportTitle: string;
  entryDate: string;
  hoursSpent: number;
  comment?: string;
}

export type PeriodFilter = 'ANNEE' | 'MENSUEL' | 'TRIMESTRIEL';
