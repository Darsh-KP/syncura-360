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
import {VisitFormComponent} from '../visit-form/visit-form.component';


@Component({
  selector: 'app-visit-mgmt',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AgGridModule,
    NavbarComponent,
    MatButtonModule,],
  templateUrl: './visit-mgmt.component.html',
  styleUrl: './visit-mgmt.component.css'
})
export class VisitMgmtComponent {
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
        return `<button class="view-button">View</button>`;
      }
    }
  ];

  ngOnInit(){
    this.loadVisits();
  }
  loadVisits(): void {
    //use the getAllVisits method from the service
    this.visitService.getVisits().subscribe({
      next: (res) => {
        this.visitList = res.visits;
        this.successMessage = '';
      },
      error: (err) => {
        this.visitList = [];
        this.errorMessage = err?.error?.message || 'Failed to load visits.';
      }
    });
  }

  openVisitForm(visit?: visit): void {
    const dialogRef = this.dialog.open(VisitFormComponent, {
      width: '500px',
      data: visit || null

    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadVisits();
    });

  }

  onGridReady(params: any) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    // Event delegation for custom button
    params.api.addEventListener('cellClicked', (event: any) => {
      if (event.colDef.headerName === 'Actions' && event.event.target.classList.contains('view-button')) {
        const { patientID, admissionDateTime } = event.data;
        this.router.navigate(['/timeline', patientID, admissionDateTime]);
      }
    });

    params.api.sizeColumnsToFit();
  }

}


