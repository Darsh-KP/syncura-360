import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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
  columnDefs: ColDef[] = [
    { field: 'patientID', headerName: 'ID' },
    { field: 'firstName', headerName: 'First Name' },
    { field: 'lastName', headerName: 'Last Name' },
    { field: 'dateOfBirth', headerName: 'DOB' },
    { field: 'gender' },
    { field: 'bloodType' },
    { field: 'height' },
    { field: 'weight' },
    {
      headerName: 'Action',
      cellRenderer: (params: any) => {
        const button = document.createElement('button');
        button.innerText = 'View';
        button.className = 'view-button';
        button.addEventListener('click', () => {
          this.dialog.open(PatientDetailsDialogComponent, {
            width: '500px',
            data: params.data
          });
        });
        return button;
      }
    }
  ];

  rowData: any[] = [];

  constructor(private http: HttpClient, public dialog: MatDialog) {}

  ngOnInit() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>('http://localhost:8080/patient/all', { headers }).subscribe(data => {
      this.rowData = data;
    });
  }
}
