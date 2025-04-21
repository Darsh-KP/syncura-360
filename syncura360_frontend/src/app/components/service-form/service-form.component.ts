import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ServiceMgmtService, service,serviceUpdateDto} from '../../services/service-mgmt.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-service-form',
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
  templateUrl: './service-form.component.html',
  styleUrl: './service-form.component.css'
})
export class ServiceFormComponent {
  serviceForm: FormGroup;
  loading = false;
  createServiceError ='';
  isEditing: boolean = false;

  category: string[]=['🩺 Diagnostic','💉 Surgical', '⛨ Emergency', 'Therapeutic','Outpatient','🚼 Maternal/Neonatal','Hospice','Preventive'];

  constructor(
    private fb: FormBuilder,
    private service_mgmtSCV: ServiceMgmtService,
    public dialogRef: MatDialogRef<ServiceFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: service | null
  ) {
    this.isEditing = !!data;

    this.serviceForm = this.fb.group({
      name: [data?.name ?? '', [Validators.required, Validators.maxLength(250)]],
      category: [data?.category ?? '', [Validators.maxLength(50)]],
      description: [data?.description ?? '', [Validators.maxLength(65535)]],
      cost: [data?.cost ?? null, [Validators.required, Validators.min(0), Validators.pattern(/^\d{1,10}(\.\d{1,2})?$/)]]
    });

    if (this.isEditing) {
      this.serviceForm.get('name')?.disable();
      this.serviceForm.get('category')?.disable();
    }
  }


  ngOnInit():void {
  }

  submit(): void {
    if (this.serviceForm.invalid) {
      this.createServiceError = 'All fields are required.';
      return;
    }

    this.loading = true;
    this.createServiceError = '';
    const rawFormData = this.serviceForm.getRawValue();

    if (this.isEditing) {
      // Ensure we have the original name - this should always exist in edit mode
      const originalName = this.data?.name;
      if (!originalName) {
        this.createServiceError = 'Cannot update: Original service name not found';
        this.loading = false;
        return;
      }

      const updateRequest = {
        name: originalName,
        updates: {
          name: rawFormData.name,
          category: rawFormData.category,
          description: rawFormData.description,
          cost: rawFormData.cost
        }
      };

      this.service_mgmtSCV.updateServices([updateRequest]).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Backend error:', error);
          this.createServiceError = error?.error?.message || 'Failed to update service.';
          this.loading = false;
        }
      });
    } else {
      // Create logic remains the same
      this.service_mgmtSCV.createService([{
        name: String(rawFormData.name),
        category: rawFormData.category,
        description: rawFormData.description,
        cost: Number(rawFormData.cost)
      }]).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Backend error:', error);
          this.createServiceError = error?.error?.message || 'Failed to create service.';
          this.loading = false;
        }
      });
    }
  }
  cancel(): void {
    this.dialogRef.close();
  }

}
