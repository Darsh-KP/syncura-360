import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { LoginComponent } from './app/components/login/login.component';
import { RegisterComponent } from './app/components/register/register.component';
import { DashboardComponent } from './app/components/dashboard/dashboard.component';
import { MatNativeDateModule } from '@angular/material/core';
import { importProvidersFrom } from '@angular/core';
import {InventoryComponent} from './app/components/inventory/inventory.component';
import { AuthGuard } from './app/guards/auth.guard';
import { routes } from './app/app.routes';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';


bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),  // Use imported routes
    provideHttpClient(withFetch()),
    provideAnimations(),
    importProvidersFrom(MatNativeDateModule),
    importProvidersFrom(CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }))
  ],
}).catch(err => console.error(err));
