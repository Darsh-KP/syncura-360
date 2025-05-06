import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RegisterPatientComponent} from './components/nurse-maint/register-patient/register-patient.component';
import { AuthGuard } from './guards/auth.guard';
import { SchedulingComponent } from './components/scheduling/scheduling.component';
import { PatientListComponent } from './components/patient-list/patient-list.component';
import {InventoryComponent} from './components/inventory/inventory.component';
import { RoomManagementComponent } from './components/room-management/room-management.component';
import { NurseScheduleComponent} from './components/nurse-maint/nurse-schedule/nurse-schedule.component';
import { DoctorScheduleComponent} from './components/doctor-maint/doctor-schedule/doctor-schedule.component';
import {ServiceMgmtComponent} from './components/service-mgmt/service-mgmt.component';
import { RoomViewComponent } from './components/room-view/room-view.component';
import {VisitMgmtComponent} from './components/visit-mgmt/visit-mgmt.component';
import {TimelineComponent} from './components/timeline/timeline.component';
import { AccountDetailsComponent } from './components/account-details/account-details.component';
import {VisitRecordsComponent} from './components/visit-records/visit-records.component';


export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard], data: {roles:['Super Admin','Admin']} },
  { path: 'scheduling', component: SchedulingComponent, canActivate: [AuthGuard],  data: {roles:['Super Admin','Admin']} },
  { path: 'inventory', component: InventoryComponent, canActivate: [AuthGuard],  data: {roles:['Super Admin','Admin']} },
  { path: 'service-mgmt', component: ServiceMgmtComponent, canActivate: [AuthGuard] },
  {path: 'room-management', component: RoomManagementComponent, canActivate: [AuthGuard],  data: {roles:['Super Admin','Admin']} },

  {path: 'account-details', component: AccountDetailsComponent, canActivate: [AuthGuard],  data: {roles:['Super Admin','Admin', 'Nurse', 'Doctor']} },


  // nurse routes
  {path: 'nurse', component: RegisterPatientComponent, canActivate: [AuthGuard], data: {roles:['Nurse']} },
  {path: 'schedule', component: NurseScheduleComponent, canActivate: [AuthGuard], data: {roles:['Nurse']} },


  // doctor routes
  {path: 'doctor', component: DoctorScheduleComponent, canActivate: [AuthGuard], data: {roles:['Doctor']} },

  { path: 'view-patients', component: PatientListComponent, canActivate: [AuthGuard], data: { roles: ['Nurse','Doctor']} },
  {path: 'register-patient', component: RegisterPatientComponent, canActivate: [AuthGuard], data: {roles:['Nurse','Doctor']}},

  // doctor routes
  {path: 'doctor', component: DoctorScheduleComponent, canActivate: [AuthGuard], data: {roles:['Doctor']} },

  // doctor/nurse routes

  {path: 'room', component: RoomViewComponent, canActivate: [AuthGuard], data: {roles:['Nurse', 'Doctor']} },
  {path: 'visit-records', component: VisitRecordsComponent, canActivate: [AuthGuard], data: {roles:['Doctor', 'Nurse']} },
  {path: 'visit-mgmt', component: VisitMgmtComponent, canActivate: [AuthGuard], data: {roles:['Nurse','Doctor']} },

  {
    path: 'timeline/:patientID/:admissionDateTime',
    component: TimelineComponent
    ,canActivate: [AuthGuard], data: {roles:['Nurse','Doctor']}
  }

];
