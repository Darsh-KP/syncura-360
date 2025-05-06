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
    'Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado',
    'Connecticut', 'Delaware', 'Florida', 'Georgia', 'Hawaii', 'Idaho',
    'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana',
    'Maine', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'Mississippi',
    'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire', 'New Jersey',
    'New Mexico', 'New York', 'North Carolina', 'North Dakota', 'Ohio',
    'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island', 'South Carolina',
    'South Dakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 'Virginia',
    'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'
  ];
  traumaLevels: string[] = ['Level I', 'Level II', 'Level III', 'Level IV', 'Level V'];

  constructor(private fb: FormBuilder, private router: Router, private registerService: RegisterService) {
    this.registerForm = this.fb.group({
      // Hospital Information
      name: ['', Validators.required],
      addressLine1: ['', Validators.required],
      addressLine2: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postal: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      telephone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      type: ['', Validators.required],
      traumaLevel: ['', Validators.required],
      hasHelipad: [false],

      // Admin (Staff) Information
      username: ['', Validators.required],
      passwordHash: ['', [Validators.required, Validators.minLength(8)]],
      role: ['Super Admin'],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],

      // Admin Address
      addressLine1Admin: ['', Validators.required],
      addressLine2Admin: ['', Validators.required],
      cityAdmin: ['', Validators.required],
      stateAdmin: ['', Validators.required],
      postalAdmin: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      country: ['', Validators.required],
      dateOfBirth: ['', Validators.required]
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
        name: this.registerForm.value.name,
        addressLine1: this.registerForm.value.addressLine1,
        addressLine2: this.registerForm.value.addressLine2,
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
        addressLine2:this.registerForm.value.addressLine2Admin,
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
        this.errorMessage = 'An error occurred while processing your request.';
        }
    });
  }

  ngOnInit() {
    this.registerForm.reset();
  }

  /**
   * Navigates back to the login page.
   */
  navigateToLogin() {
    this.registerForm.reset();
    this.router.navigate(['/']);
  }
}
