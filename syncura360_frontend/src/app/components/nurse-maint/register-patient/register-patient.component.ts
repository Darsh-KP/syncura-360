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

@Component({
  selector: 'app-register-patient',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
      NavbarComponent,
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

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
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

    this.loading = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const patientData = this.patientForm.value;

    this.http.post('http://localhost:8080/patient', patientData, { headers }).subscribe({
      next: (response: any) => {
        this.successMessage = response?.message || 'Patient registered successfully.';
        this.loading = false;
        this.patientForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'An error occurred while registering the patient.';
        this.loading = false;
      }
    });
  }
}
