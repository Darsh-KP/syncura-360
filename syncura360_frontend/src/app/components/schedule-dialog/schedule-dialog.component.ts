import { Component, Inject, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { Schedule, ScheduleService } from '../../services/schedule.service';
import { StaffService, Staff } from '../../services/staff.service';
import { MatFormField } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-schedule-dialog',
  standalone: true,
  templateUrl: './schedule-dialog.component.html',
  styleUrls: ['./schedule-dialog.component.css'],
  imports: [
    CommonModule,
    MatDialogModule, 
    ReactiveFormsModule,
    MatFormField,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
})
export class ScheduleDialogComponent implements AfterViewInit {
  scheduleForm: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';
  minDate = new Date();
  usernamesList: string[] = [];

  constructor(
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
    private staffService: StaffService,
    public dialogRef: MatDialogRef<ScheduleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schedule: Schedule | null }
  ) {
    this.scheduleForm = this.fb.group({
      username: [data.schedule?.username || '', Validators.required],
      date: [data.schedule ? new Date(data.schedule.start) : '', Validators.required],
      startTime: [data.schedule ? this.extractTime(data.schedule.start) : '', Validators.required],
      endTime: [data.schedule ? this.extractTime(data.schedule.end) : '', Validators.required],
      department: [data.schedule?.department || '']
    }, { validators: this.validateTimeOrder });    
  }
  
  ngOnInit(): void {
    this.fetchUsernames();  
  }
  
  ngAfterViewInit(): void {
    setTimeout(() => {
      const inputEl = document.querySelector('input[formcontrolname="date"]') as HTMLElement;
      inputEl?.focus();
    });
  }

  extractTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', hour12: false });
  }  

  fetchUsernames() {
    this.staffService.getAllStaff().subscribe({
      next: (staff) => {
        this.usernamesList = staff.map(s => s.username);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to load staff usernames.';
      }
    });
  }
  
  saveSchedule() {
    if (this.scheduleForm.invalid) {
      console.warn('Form is invalid:', this.scheduleForm.errors, this.scheduleForm.value);
      return;
    }
  
    const formValue = this.scheduleForm.value;
    const formattedDate = this.formatDate(formValue.date);
    const updatedSchedule: Schedule = {
      username: formValue.username,
      start: `${formattedDate}T${formValue.startTime}:00`,
      end: `${formattedDate}T${formValue.endTime}:00`,
      department: formValue.department
    };
  
    this.loading = true;
  
    const request$ = this.data.schedule
      ? this.scheduleService.updateSchedule([
          {
            id: {
              username: this.data.schedule.username,
              start: this.data.schedule.start
            },
            updates: updatedSchedule
          }
        ])
      : this.scheduleService.createSchedule([updatedSchedule]);
  
    request$.subscribe({
      next: (res) => {
        this.successMessage = res.message;
        this.dialogRef.close('updated');
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to save schedule.';
        this.loading = false;
      }
    });
  }
  

  deleteSchedule() {
    if (!this.data.schedule) return;

    const confirmDelete = confirm('Are you sure you want to delete this schedule?');
    if (!confirmDelete) return;

    this.loading = true;

    this.scheduleService.deleteSchedule([{
      start: this.data.schedule.start,
      username: this.data.schedule.username
    }]).subscribe({
      next: (res) => {
        this.successMessage = res.message;
        this.dialogRef.close('deleted');
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to delete schedule.';
        this.loading = false;
      }
    });
  }

  formatDate(date: any): string {
    const d = new Date(date); // Force to Date object
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  validateTimeOrder(group: FormGroup) {
    const start = group.get('startTime')?.value;
    const end = group.get('endTime')?.value;
  
    if (start && end && start >= end) {
      return { invalidTimeOrder: true };
    }
    return null;
  }

  closeDialog() {
    this.dialogRef.close();
  }
}