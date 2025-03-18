import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterService } from '../../services/register.service';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { provideHttpClient } from '@angular/common/http';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  passwordVisible: boolean = false;
  loading = false;
  submitted = false;

  states: string[] = [
    'AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD',
    'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC',
    'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY'
  ];
  traumaLevels: string[] = ['Level I', 'Level II', 'Level III', 'Level IV', 'Level V'];

  constructor(private fb: FormBuilder, private router: Router, private registerService: RegisterService) {
    this.registerForm = this.fb.group({
      // Hospital Information
      hospitalName: ['NJ General Hospital', Validators.required],
      addressLine1: ['123 Main St', Validators.required],
      city: ['New Jersey', Validators.required],
      state: ['NJ', Validators.required],
      postal: ['10001', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      telephone: ['4738258383', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      type: ['General', Validators.required],
      traumaLevel: ['Level I', Validators.required],
      hasHelipad: [true],
  
      // Admin (Staff) Information
      username: ['admin123', Validators.required],
      passwordHash: ['123456', [Validators.required, Validators.minLength(6)]],
      role: ['Super Admin'],
      firstName: ['John', Validators.required],
      lastName: ['Doe', Validators.required],
      email: ['john.doe@example.com', [Validators.required, Validators.email]],
      phone: ['2382838233', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      
      // Admin Address
      addressLine1Admin: ['456 Elm St', Validators.required],
      cityAdmin: ['Los Angeles', Validators.required],
      stateAdmin: ['CA', Validators.required],
      postalAdmin: ['90001', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      country: ['United States', Validators.required],
      dateOfBirth: ['1990-01-01', Validators.required] // Make sure date format matches what your input expects
    });
  }
  

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }

  getInvalidControls() {
    const invalid = [];
    const controls = this.registerForm.controls;
    for (const name in controls) {
      if (controls[name].invalid) {
        invalid.push({ field: name, errors: controls[name].errors });
      }
    }
    return invalid;
  }
  

  /**
   * Handles form submission, validates data, and sends the registration request.
   */
  onSubmit() {
    this.submitted = true;
    
    // Log form values for debugging
    console.log('Form Values:', this.registerForm.value);
    
  
    if (this.registerForm.invalid) {
      console.log('Form Errors:', this.getInvalidControls());
      this.errorMessage = 'All fields must be correctly filled to register';
      return;
    }
  
    this.loading = true;
    this.errorMessage = '';
  
    const requestBody = {
      hospital: {
        name: this.registerForm.value.hospitalName,
        addressLine1: this.registerForm.value.addressLine1,
        city: this.registerForm.value.city,
        state: this.registerForm.value.state,
        postal: this.registerForm.value.postal,
        telephone: this.registerForm.value.telephone,
        type: this.registerForm.value.type,
        traumaLevel: this.registerForm.value.traumaLevel,
        hasHelipad: this.registerForm.value.hasHelipad
      },
      staff: {
        username: this.registerForm.value.username,
        passwordHash: this.registerForm.value.passwordHash,
        role: 'Super Admin',
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
        email: this.registerForm.value.email,
        phone: this.registerForm.value.phone,
        addressLine1: this.registerForm.value.addressLine1Admin,
        city: this.registerForm.value.cityAdmin,
        state: this.registerForm.value.stateAdmin,
        postal: this.registerForm.value.postalAdmin,
        country: this.registerForm.value.country,
        dateOfBirth: this.registerForm.value.dateOfBirth
      }
    };
  
    console.log('Submitting Request:', requestBody);
  
    this.registerService.registerHospital(requestBody).subscribe({
      next: (response) => {
        console.log('Response:', response);
        this.successMessage = 'Registration successful! Redirecting to login page...';
        this.loading = false;
  
        setTimeout(() => {
          this.registerForm.reset();
          this.router.navigate(['/']);
        }, 3000);
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.loading = false;
        this.successMessage = '';
        this.errorMessage = err.message || 'An error occurred while processing your request.';
      }
    });
  }
  
  
  /**
   * Navigates back to the login page.
   */
  navigateToLogin() {
    this.registerForm.reset();
    this.router.navigate(['/']);
  }
}
