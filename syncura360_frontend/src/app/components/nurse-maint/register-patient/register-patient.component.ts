import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import {NavbarComponent} from "../../navbar/navbar.component";
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar} from "@angular/material/snack-bar";
import { MatSnackBarModule} from "@angular/material/snack-bar";




@Component({
  selector: 'app-register-patient',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
      NavbarComponent,
      FormsModule,
      MatSlideToggleModule,
      MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
  ],
  templateUrl: './register-patient.component.html',
  styleUrls: ['./register-patient.component.css']
})
export class RegisterPatientComponent {
  patientForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';
  loading = false;

  bloodTypes: string[] = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
  genders: string[] = ['Male', 'Female'];

  darkMode = false;

  toggleDarkMode() {
    if (this.darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router, private snackBar: MatSnackBar) {
    this.patientForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      bloodType: [''],
      height: [''],
      weight: [''],
      phone: ['', Validators.required],
      addressLine1: ['', Validators.required],
      addressLine2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postal: ['', Validators.required],
      country: ['', Validators.required],
      emergencyContactName: [''],
      emergencyContactPhone: ['']
    });
  }

  onSubmit() {
    if (this.patientForm.invalid) {
      this.errorMessage = 'Please fill all required fields correctly.';
      return;
    }

    const snackBarRef = this.snackBar.open('Submit this patient info?', 'Confirm', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
    snackBarRef.onAction().subscribe(() => {
      this.submitPatient();
    });
  }

  formatDate(date: any): string {
    const d = new Date(date);
    return d.toISOString().split('T')[0];
  }
  
  submitPatient() {
    this.loading = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  
    const formValue = {...this.patientForm.value};
    formValue.dateOfBirth = this.formatDate(formValue.dateOfBirth);
  
    // Convert empty strings to null
    Object.keys(formValue).forEach(key => {
      if (formValue[key] === '') {
        formValue[key] = null;
      }
    });
  
    this.http.post('http://localhost:8080/patient', formValue, {headers}).subscribe({
      next: (response: any) => {
        this.successMessage = response?.message || 'Patient registered successfully.';
        this.snackBar.open('Patient added successfully!', 'Close', {duration: 3000});
        this.loading = false;
        this.patientForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'An error occurred while registering the patient.';
        this.snackBar.open('Error adding patient', 'Close', {duration: 3000});
        this.loading = false;
      }
    });
  }
  
}
