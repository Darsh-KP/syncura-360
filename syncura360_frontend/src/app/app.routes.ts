import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RegisterPatientComponent} from './components/nurse-maint/register-patient/register-patient.component';
import { AuthGuard } from './guards/auth.guard';
import { SchedulingComponent } from './components/scheduling/scheduling.component';
import {InventoryComponent} from './components/inventory/inventory.component';
import { RoomManagementComponent } from './components/room-management/room-management.component';
import {ServiceMgmtComponent} from './components/service-mgmt/service-mgmt.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'scheduling', component: SchedulingComponent, canActivate: [AuthGuard] },
  { path: 'inventory', component: InventoryComponent, canActivate: [AuthGuard] },
  { path: 'service-mgmt', component: ServiceMgmtComponent, canActivate: [AuthGuard] },
  {path: 'room-management', component: RoomManagementComponent, canActivate: [AuthGuard] },
  {
    path: 'nurse',
    component: RegisterPatientComponent,
    canActivate: [AuthGuard],
    data: {roles:['Nurse']}
  }
];
