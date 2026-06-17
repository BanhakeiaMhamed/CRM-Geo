export type ReportStatus =
  | 'BROUILLON' | 'EN_COURS' | 'EN_VALIDATION' | 'VALIDE'
  | 'EN_RECTIFICATION' | 'ENVOYE' | 'CLOTURE';

export type ReportType =
  | 'G1_ES' | 'G1_PGC'
  | 'G2_AVP' | 'G2_PRO' | 'G2_DCE_ACT'
  | 'G3_PHASE_ETUDE' | 'G3_PHASE_SUIVI'
  | 'G4_SUPERVISION_ETUDE' | 'G4_SUPERVISION_SUIVI'
  | 'G5_DIAGNOSTIC';

export const MIN_PAGES_FOR_VALIDATION = 10;

export interface Report {
  id: number;
  reference: string;
  title: string;
  reportType: ReportType;
  status: ReportStatus;
  revenueAmount: number;
  engineerEmail: string;
  tags: string[];
  address: string;
  postalCode: string;
  city: string;
  latitude?: number;
  longitude?: number;
  pageCount: number;
  pdfFileName?: string;
  hasPdf: boolean;
}

export interface InterventionMapItem {
  reference: string;
  title: string;
  reportType: ReportType;
  status: ReportStatus;
  address: string;
  postalCode: string;
  city: string;
  latitude: number;
  longitude: number;
  pageCount: number;
  engineerFirstName: string;
  engineerLastName: string;
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
  EN_COURS: '#FF7A59',
  EN_VALIDATION: '#FFC857',
  VALIDE: '#00BDA5',
  ENVOYE: '#6A78D1',
  CLOTURE: '#7C98B6'
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

export const ENGINEER_CHART_COLORS = ['#FF7A59', '#00BDA5', '#6A78D1', '#FFC857', '#F2547D'];

export const COLUMN_COLORS: Partial<Record<ReportStatus, string>> = {
  EN_COURS: '#FF7A59',
  EN_VALIDATION: '#FFC857',
  EN_RECTIFICATION: '#F2547D',
  VALIDE: '#00BDA5',
  ENVOYE: '#6A78D1',
  CLOTURE: '#7C98B6'
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
  G1_ES: 'G1 ES — Etude de Site',
  G1_PGC: 'G1 PGC — Principes Generaux de Construction',
  G2_AVP: 'G2 AVP — Avant-Projet',
  G2_PRO: 'G2 PRO — Projet',
  G2_DCE_ACT: 'G2 DCE/ACT — Consultation / Assistance contrats',
  G3_PHASE_ETUDE: 'G3 — Phase Etude',
  G3_PHASE_SUIVI: 'G3 — Phase Suivi',
  G4_SUPERVISION_ETUDE: 'G4 — Supervision etude d\'execution',
  G4_SUPERVISION_SUIVI: 'G4 — Supervision suivi d\'execution',
  G5_DIAGNOSTIC: 'G5 — Diagnostic geotechnique'
};

export interface ReportTypeGroup {
  label: string;
  types: ReportType[];
}

export const REPORT_TYPE_GROUPS: ReportTypeGroup[] = [
  {
    label: 'G1 — Etude geotechnique prealable',
    types: ['G1_ES', 'G1_PGC']
  },
  {
    label: 'G2 — Etude geotechnique de conception',
    types: ['G2_AVP', 'G2_PRO', 'G2_DCE_ACT']
  },
  {
    label: 'G3 — Etude et suivi geotechniques d\'execution',
    types: ['G3_PHASE_ETUDE', 'G3_PHASE_SUIVI']
  },
  {
    label: 'G4 — Supervision geotechnique d\'execution',
    types: ['G4_SUPERVISION_ETUDE', 'G4_SUPERVISION_SUIVI']
  },
  {
    label: 'G5 — Diagnostic geotechnique',
    types: ['G5_DIAGNOSTIC']
  }
];

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
