import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import {VisitMgmtService, newvisit} from '../../services/visit-mgmt.service';


@Component({
  selector: 'app-visit-form',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule],
  providers: [
    MatNativeDateModule,
    MatDatepickerModule,
    { provide: MAT_DATE_LOCALE, useValue: 'en-US' }
  ],
  templateUrl: './visit-form.component.html',
  styleUrl: './visit-form.component.css'
})
export class VisitFormComponent {
  visitForm: FormGroup;
  loading = false;
  createVisitError='';
  isEditing:boolean = false;

  constructor(
    private fb: FormBuilder,
    private visitSVC: VisitMgmtService,
    public dialogRef: MatDialogRef<VisitFormComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: newvisit | null
  ){
    this.isEditing = !!data;

    this.visitForm = this.fb.group({
        patientID: [data?.patientID ?? null, [Validators.required, Validators.min(0), Validators.pattern(/^\d{1,10}?$/)]],
        reasonForVisit: [data?.reasonForVisit ?? '', [Validators.maxLength(65535)]],
    });
  }

  submit(){
    if (this.visitForm.invalid) return;

    const payload = this.visitForm.value as newvisit;

    this.loading = true;
    this.visitSVC.createVisit(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.dialogRef.close(true); // Return success to parent component
      },
      error: (err) => {
        this.loading = false;
        this.createVisitError = err?.error?.message || 'Failed to create visit. Please try again.';
      }
    });
  }
}
