import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgGridModule } from 'ag-grid-angular';
import {ColDef, GridReadyEvent, GridOptions} from 'ag-grid-community';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { PatientDetailsDialogComponent } from '../patient-details-dialog/patient-details-dialog.component';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, AgGridModule, NavbarComponent],
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.css']
})
export class PatientListComponent {
  gridAPI: any;
  rowData: any[] = [];

  gridOptions: GridOptions<any> = {
    defaultColDef: {
      filter: true,
      sortable: true,
      resizable: true,
      minWidth: 120
    }
  };

   columnDefs: ColDef[] = [
      { field: 'patientId', headerName: 'ID' },
      { field: 'firstName', headerName: 'First Name' },
      { field: 'lastName', headerName: 'Last Name' },
      { field: 'dateOfBirth', headerName: 'DOB' },
      { field: 'gender' },
      { field: 'phone', headerName: 'Phone Number' },
      { field: 'addressLine1', headerName: 'Address' },
      { field: 'addressLine2', headerName: 'Address Cont.' },
      { field: 'city', headerName: 'City' },
      { field: 'state', headerName: 'State' },
      { field: 'postal', headerName: 'Zip Code' },
      { field: 'country', headerName: 'Country' },
      {
        headerName: 'Action',
        pinned: 'right',
        cellRenderer: (params: any) => {
          return `<button class="view-btn px-2 py-1 text-xs bg-blue-500 text-white rounded hover:bg-blue-600">View</button>`;
        }
      }
      ];

  constructor(private http: HttpClient, public dialog: MatDialog, private router: Router) {}

  ngOnInit() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No token found. Redirecting to login...');
      this.router.navigate(['/']);
      return;
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any>('http://localhost:8080/patient', {headers}).subscribe({
      next: data => {
        console.log('Patient response:', data);
        this.rowData = data.patients;
      },
      error: err => {
        if (err.status === 401) {
          console.warn('Unauthorized - redirecting to login.');
          this.router.navigate(['/']);
        }
      }
    });
  }

  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridAPI.setQuickFilter(filterValue);
  }

  onGridReady(params: GridReadyEvent){
    this.gridAPI = params.api;
    const allColumnIds: string[] = [];
    params.columnApi.getColumns()?.forEach((col) => {
      allColumnIds.push(col.getId());
    });
    params.columnApi.autoSizeColumns(allColumnIds);
  }

  onCellClicked(event: any) {
    if (event.colDef && event.colDef.headerName == 'Action' && event.event.target.classList.contains('view-btn')) {
      this.dialog.open(PatientDetailsDialogComponent, {
        width: '500px',
        data: event.data
      });
    }
    }
    }
