import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StaffService, Staff } from '../../services/staff.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-user-maint',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './user-maint.component.html',
  styleUrls: ['./user-maint.component.css'],
})
export class UserMaintComponent implements OnInit {
  staffForm: FormGroup;
  loading = false;
  createStaffError = '';

  constructor(
    private fb: FormBuilder,
    private staffService: StaffService,
    public dialogRef: MatDialogRef<UserMaintComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Staff | null
  ) {
    this.staffForm = this.fb.group({
      username: [data?.username || '', Validators.required],
      passwordHash: ['', [Validators.required, Validators.minLength(6)]],
      role: [data?.role || '', Validators.required],
      firstName: [data?.firstName || '', Validators.required],
      lastName: [data?.lastName || '', Validators.required],
      email: [data?.email || '', [Validators.required, Validators.email]],
      phone: [data?.phone || '', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      addressLine1: [data?.addressLine1 || '', Validators.required],
      addressLine2: [data?.addressLine2 || '', Validators.required],
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

    const staffPayload = [this.staffForm.value];

    this.staffService.createStaff(staffPayload).subscribe({
      next: (response) => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.createStaffError = error.message || 'Failed to create staff.';
        this.loading = false;
      }
    });
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
