import { Component } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {ICellRendererAngularComp} from 'ag-grid-angular';
import { CommonModule } from '@angular/common';
import { PatientDetailsDialogComponent } from '../../patient-details-dialog/patient-details-dialog.component';

@Component({
  selector: 'app-view-button',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  template: `
    <button class="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
            (click)="openDialog()">
      View
    </button>
  `
})
export class ViewButtonComponent implements ICellRendererAngularComp{
  params: any;

  constructor(private dialog: MatDialog) {}

  agInit(params: any) {
    this.params = params;
  }

  refresh(params: any): boolean {
    this.params = params;
    return true;
  }

  openDialog() {
    this.dialog.open(PatientDetailsDialogComponent, {
      width: '500px',
      data: this.params.data
    });
  }
}
