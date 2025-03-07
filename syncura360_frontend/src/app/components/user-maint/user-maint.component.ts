import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StaffService, Staff } from '../../services/staff.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-user-maint',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  providers: [
    MatNativeDateModule,
    MatDatepickerModule,
    { provide: MAT_DATE_LOCALE, useValue: 'en-US' }
  ],
  templateUrl: './user-maint.component.html',
  styleUrls: ['./user-maint.component.css'],
})
export class UserMaintComponent implements OnInit {
  staffForm: FormGroup;
  loading = false;
  createStaffError = '';
  passwordVisible: boolean = false;
  isEditing: boolean = false;

  states: string[] = [
    'AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD',
    'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC',
    'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY'
  ];

  constructor(
    private fb: FormBuilder,
    private staffService: StaffService,
    public dialogRef: MatDialogRef<UserMaintComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Staff | null
  ) {
    this.isEditing = !!data; // Determines if we are editing an existing staff member

    this.staffForm = this.fb.group({
      id: [data?.id || null],
      username: [data?.username || '', Validators.required],

      // Only add password field if creating a new staff member
      ...(this.isEditing ? {} : { passwordHash: ['', [Validators.minLength(6)]]}),

      role: [data?.role || '', Validators.required],
      firstName: [data?.firstName || '', Validators.required],
      lastName: [data?.lastName || '', Validators.required],
      email: [data?.email || '', [Validators.required, Validators.email]],
      phone: [data?.phone || '', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      addressLine1: [data?.addressLine1 || '', Validators.required],
      addressLine2: [data?.addressLine2 || ''],
      city: [data?.city || '', Validators.required],
      state: [data?.state || '', Validators.required],
      postal: [data?.postal || '', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      country: [data?.country || '', Validators.required],
      dateOfBirth: [data?.dateOfBirth || '', Validators.required]
    });
  }

  ngOnInit(): void {}

  submitForm() {
    if (this.staffForm.invalid) {
      this.createStaffError = 'All fields are required.';
      return;
    }

    this.loading = true;
    this.createStaffError = '';

    const formData = { ...this.staffForm.value };

    if (this.isEditing) {
      // If editing, update staff member
      const updatePayload = [
        {
          id: formData.id,
          fields: {
            username: formData.username,
            role: formData.role,
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone,
            addressLine1: formData.addressLine1,
            addressLine2: formData.addressLine2,
            city: formData.city,
            state: formData.state,
            postal: formData.postal,
            country: formData.country,
            dateOfBirth: formData.dateOfBirth
          }
        }
      ];

      this.staffService.updateStaff(updatePayload).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          this.createStaffError = error.message || 'Failed to update staff.';
          this.loading = false;
        }
      });

    } else {
      // If creating a new staff member
      const staffPayload = [this.staffForm.value];
      this.staffService.createStaff(staffPayload).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          this.createStaffError = error.message || 'Failed to create staff.';
          this.loading = false;
        }
      });
    }
  }

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
