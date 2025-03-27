import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { inventoryService, inventory} from '../../services/inventory.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-item-form',
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
  templateUrl: './item-form.component.html',
  styleUrls: ['./item-form.component.css']
})
export class ItemFormComponent {
  itemForm: FormGroup;
  loading = false;
  createItemError ='';
  isEditing: boolean = false;

  category: string[]=['Drug','Medical Supply'];

  constructor(
    private fb: FormBuilder,
    private inventoryService: inventoryService,
    public dialogRef: MatDialogRef<ItemFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: inventory | null
  ) {
    this.isEditing = !!data;

    this.itemForm = this.fb.group({
      ndc: [data?.ndc ?? null, [Validators.required, Validators.pattern(/^\d{11}$/)]],
      name: [data?.name ?? '', [Validators.required, Validators.maxLength(250)]],
      category: [data?.category ?? '', [Validators.maxLength(50)]],
      description: [data?.description ?? '', [Validators.maxLength(65535)]],
      strength: [data?.strength ?? '', [Validators.maxLength(50)]],
      ppq: [data?.ppq ?? null, [Validators.min(0)]], // optional field, not required
      quantity: [data?.quantity ?? null, [Validators.required, Validators.min(0)]],
      price: [data?.price ?? null, [Validators.required, Validators.min(0), Validators.pattern(/^\d{1,10}(\.\d{1,2})?$/)]]
    });

    if (this.isEditing) {
      this.itemForm.get('ndc')?.disable();
      this.itemForm.get('name')?.disable();
      this.itemForm.get('category')?.disable();
      this.itemForm.get('description')?.disable();
      this.itemForm.get('strength')?.disable();
      this.itemForm.get('ppq')?.disable();
    }
  }

  ngOnInit():void {
    this.itemForm.get('category')?.valueChanges.subscribe(value => {
      const strengthControl = this.itemForm.get('strength');

      if (value === 'Medical Supply') {
        strengthControl?.reset();
        strengthControl?.disable();
      } else {
        strengthControl?.enable();
      }
    });

    // Handle initial value (for edit case)
    const initialCategory = this.itemForm.get('category')?.value;
    if (initialCategory === 'Medical Supply') {
      this.itemForm.get('strength')?.disable();
    }
  }


  submit(): void {
    if (this.itemForm.invalid) {
      this.createItemError = 'All fields are required.';
      return;
    }

    this.loading = true;
    this.createItemError = '';

    const rawFormData = this.itemForm.getRawValue(); // ✅ includes disabled fields

    const formData = {
      ...rawFormData,
      ndc: Number(rawFormData.ndc),
      ppq: rawFormData.ppq ? Number(rawFormData.ppq) : null,
      quantity: Number(rawFormData.quantity),
      price: Number(rawFormData.price),
    };

    console.log('Submitting form data:', formData); // ✅ Debug log

    if (this.isEditing) {
      // ✅ Edit flow: send single object
      this.inventoryService.updateInventoryQuantities(formData).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Backend error:', error);
          this.createItemError = error?.error?.message || 'Failed to update item.';
          this.loading = false;
        }
      });
    } else {
      // ✅ Create flow: send single item wrapped if needed
      console.log('Submitting payload to /drug:', formData);
      this.inventoryService.createInventory(formData).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Backend error:', error);
          this.createItemError = error?.error?.message || 'Failed to create item.';
          this.loading = false;
        }
      });
    }
  }



  cancel(): void {
    this.dialogRef.close();
  }
}
