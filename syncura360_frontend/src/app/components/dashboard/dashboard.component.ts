import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StaffService, Staff } from '../../services/staff.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  staffList: Staff[] = [];
  staffForm: FormGroup;
  errorMessage = '';
  successMessage = '';
  loading = false;

  roles = ['Admin', 'Doctor', 'Nurse'];

  constructor(private staffService: StaffService, private fb: FormBuilder, private router: Router) {
    this.staffForm = this.fb.group({
      username: ['', Validators.required],
      passwordHash: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      addressLine1: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postal: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      country: ['', Validators.required],
      dateOfBirth: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.fetchStaff();
  }

  fetchStaff() {
    this.staffService.getAllStaff().subscribe({
      next: (staff) => {
        this.staffList = staff;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to load staff.';
      }
    });
  }

  addStaff() {
    if (this.staffForm.invalid) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newStaff: Staff = this.staffForm.value;

    this.staffService.createStaff(newStaff).subscribe({
      next: (response) => {
        this.successMessage = response.message || 'Staff created successfully.';
        this.staffForm.reset();
        this.fetchStaff();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.loading = false;
      }
    });
  }

  logoutAndRedirect() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/']);
  }
}