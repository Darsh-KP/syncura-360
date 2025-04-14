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
    FormsModule
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
    if (this.newPassword !== this.confirmPassword) {
      this.passwordChangeMessage = "New passwords don't match.";
      return;
    }

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
}