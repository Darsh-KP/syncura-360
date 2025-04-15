import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { AccountSettingsService, HospitalInfo, StaffInfo } from '../../services/account-settings.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-account-details',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    MatIconModule
  ],
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.css']
})
export class AccountDetailsComponent implements OnInit {
  hospitalInfo: HospitalInfo | null = null;
  staffInfo: StaffInfo | null = null;

  // Password change form
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  passwordChangeMessage: string = '';
  passwordVisibleNew: boolean = false;
  passwordVisibleConfirm: boolean = false;

  @ViewChild('passwordDialog') passwordDialog!: TemplateRef<any>;

  constructor(private accountService: AccountSettingsService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.getHospitalInfo();
    this.getStaffInfo();
  }

  getHospitalInfo() {
    this.accountService.getHospitalInfo().subscribe({
      next: (data) => (this.hospitalInfo = data),
      error: (err) => console.error('Failed to load hospital info', err)
    });
  }

  getStaffInfo() {
    this.accountService.getStaffInfo().subscribe({
      next: (data) => (this.staffInfo = data),
      error: (err) => console.error('Failed to load staff info', err)
    });
  }

  openPasswordDialog() {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.passwordChangeMessage = '';
    this.dialog.open(this.passwordDialog);
  }

  changePassword() {
    // Trim and update the values directly
    this.currentPassword = this.currentPassword.trim();
    this.newPassword = this.newPassword.trim();
    this.confirmPassword = this.confirmPassword.trim();
  
    // Validate: current password not empty
    if (!this.currentPassword) {
      this.passwordChangeMessage = "Enter current password.";
      return;
    }
  
    // Validate: new password not empty
    if (!this.newPassword) {
      this.passwordChangeMessage = "Enter a new password.";
      return;
    }
  
    // Validate: confirm new password not empty
    if (!this.confirmPassword) {
      this.passwordChangeMessage = "Confirm your new password.";
      return;
    }
  
    // Validate: new password matches confirmation
    if (this.newPassword !== this.confirmPassword) {
      this.passwordChangeMessage = "New passwords don't match.";
      return;
    }
  
    // Validate: check that new password is different from the current password
    if (this.currentPassword === this.newPassword) {
      this.passwordChangeMessage = "New password must be different from the current password.";
      return;
    }

    // All validations passed
    this.accountService.changePassword({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: (res) => {
        this.passwordChangeMessage = res.message;
        setTimeout(() => this.dialog.closeAll(), 1000);
      },
      error: (err) => {
        this.passwordChangeMessage = err?.error?.message || 'Password change failed.';
      }
    });
  }  
  
  

  closeDialog() {
    this.dialog.closeAll();
  }

  togglePasswordNew() {
    this.passwordVisibleNew = !this.passwordVisibleNew;
  }
  
  togglePasswordConfirm() {
    this.passwordVisibleConfirm = !this.passwordVisibleConfirm;
  }
  
}