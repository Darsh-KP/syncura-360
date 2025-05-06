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
    <button class="view-btn"
            (click)="openDialog()">
      View
    </button>
  `,
  styles: [`
    .view-btn {
      background-color: #F4A261;
      color: white;
      border: none;
      padding: 4px 10px;
      border-radius: 4px;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.3s ease;
      line-height: 1;
    }

    .view-btn:hover {
      background-color: #E76F51;
    }
  `]
})
export class ViewButtonComponent implements ICellRendererAngularComp {
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

