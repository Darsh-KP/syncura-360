import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { LoginComponent } from './app/components/login/login.component';
import { RegisterComponent } from './app/components/register/register.component';
import { DashboardComponent } from './app/components/dashboard/dashboard.component';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: '', component: LoginComponent }, // Default Login Page
      { path: 'register', component: RegisterComponent }, 
      { path: 'dashboard', component: DashboardComponent }, 
    ]),
    provideHttpClient(),
    provideAnimations(),
  ],
}).catch(err => console.error(err));
