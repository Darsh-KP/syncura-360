import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { Schedule } from '../../services/schedule.service';
import { MatFormField } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepicker, MatDatepickerModule } from '@angular/material/datepicker';
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
    MatDialogModule,
    MatFormField,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
})
export class ScheduleDialogComponent {
  scheduleForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ScheduleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schedule: Schedule | null }
  ) {
    this.scheduleForm = this.fb.group({
      username: [data.schedule?.username || '', Validators.required],
      date: [data.schedule?.start.split('T')[0] || '', Validators.required],
      startTime: [data.schedule?.start.split('T')[1] || '', Validators.required],
      endTime: [data.schedule?.end.split('T')[1] || '', Validators.required],
      department: [data.schedule?.department || '']
    });
  }

  saveSchedule() {
    if (this.scheduleForm.valid) {
      this.dialogRef.close(this.scheduleForm.value);
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }
}
