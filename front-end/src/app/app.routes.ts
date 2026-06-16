import { Routes } from '@angular/router';
import { authGuard } from './shared/guards/auth.guard';
import { LoginComponent } from './shared/login/login.component';
import { HomeComponent } from './home/home.component';
import { KanbanComponent } from './kanban/kanban.component';
import { KpiDashboardComponent } from './dashboard/kpi-dashboard.component';
import { ReportCreateComponent } from './reports/report-create.component';
import { TimeTrackingComponent } from './time/time-tracking.component';
import { ClientsComponent } from './clients/clients.component';
import { EngineersComponent } from './engineers/engineers.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'kanban', component: KanbanComponent, canActivate: [authGuard] },
  { path: 'reports/new', component: ReportCreateComponent, canActivate: [authGuard] },
  { path: 'time', component: TimeTrackingComponent, canActivate: [authGuard] },
  { path: 'clients', component: ClientsComponent, canActivate: [authGuard] },
  { path: 'engineers', component: EngineersComponent, canActivate: [authGuard] },
  { path: 'dashboard', component: KpiDashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'home' }
];
