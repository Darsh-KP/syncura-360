import {Component, inject, OnInit} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {NavbarComponent} from '../navbar/navbar.component'
import {ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {VisitMgmtService, visit} from '../../services/visit-mgmt.service';

@Component({
  selector: 'app-visit-records',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AgGridModule,
    NavbarComponent,
    MatButtonModule,],
  templateUrl: './visit-records.component.html',
  styleUrl: './visit-records.component.css'
})
export class VisitRecordsComponent {
  private dialog = inject(MatDialog);
  private visitService = inject(VisitMgmtService);
  constructor(private router: Router) {}
  gridColumnApi: any;
  visitList: visit[] = [];
  errorMessage = '';

  successMessage = '';
  gridApi: any;
  public themeClass: string = 'ag-theme-alpine';
  columnDefs:ColDef[] = [
    { field: 'patientID', headerName: 'Patient ID' },
    { field: 'firstName', headerName: 'First Name' },
    { field: 'lastName', headerName: 'Last Name' },
    { field: 'admissionDateTime', headerName: 'Admission Time' },
    {
      headerName: 'Actions',
      cellRenderer: (params: any) => {
        return `<button class="view-button px-3 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">View</button>`;
      }
    }
  ];

  ngOnInit(){
    this.loadVisits();
  }
  loadVisits(): void {
    //use the getAllVisits method from the service
    this.visitService.getRecords().subscribe({
      next: (res) => {
        this.visitList = Array.isArray(res) ? res : res.visits;
        this.successMessage = '';
      },
      error: (err) => {
        this.visitList = [];
        this.errorMessage = err?.error?.message || 'Failed to load visits.';
      }
    });
  }


  onGridReady(params: any) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    // Event delegation for custom button
    params.api.addEventListener('cellClicked', (event: any) => {
      if (event.colDef.headerName === 'Actions' && event.event.target.classList.contains('view-button')) {
        const { patientID, admissionDateTime } = event.data;
        this.router.navigate(['/timeline', patientID, admissionDateTime], { state: { readOnly: true } });
      }
    });

    params.api.sizeColumnsToFit();
  }

}


