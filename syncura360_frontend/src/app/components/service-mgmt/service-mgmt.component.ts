import {Component, inject, OnInit} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { StaffService, Staff } from '../../services/staff.service';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { UserMaintComponent } from '../user-maint/user-maint.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import {ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {ServiceMgmtService, service} from '../../services/service-mgmt.service';
import {ServiceFormComponent} from '../service-form/service-form.component';


@Component({
  selector: 'app-service-mgmt',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AgGridModule,
    NavbarComponent,
    MatButtonModule],
  templateUrl: './service-mgmt.component.html',
  styleUrl: './service-mgmt.component.css'
})
export class ServiceMgmtComponent {
  private dialog = inject(MatDialog);
  private service_mgmtSvc = inject(ServiceMgmtService);
  serviceList: service[] = [];
  errorMessage = '';

  successMessage = '';
  gridApi: any;
  public themeClass: string = 'ag-theme-alpine';
  columnDefs: ColDef[] = [
    { headerName: 'Name', field: 'name', sortable: true, filter: true, flex: 1 },
    { headerName: 'Category', field: 'category', sortable: true, filter: true, flex: 1 },
    { headerName: 'Description', field: 'description', sortable: true, filter: true, flex: 2 },
    { headerName: 'Cost', field: 'cost', sortable: true, filter: true, flex: 1 },
    {
      headerName: 'Actions',
      field: 'actions',
      flex: 1,
      suppressSizeToFit: false,
      cellRenderer: (params: any) => `
        <button class="edit-btn bg-blue-500 text-white px-2 py-1 rounded mr-4 hover:bg-blue-600" data-id="${params.data.id}">
          Edit
        </button>
        <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600" data-id="${params.data.name}">
          Delete
        </button>
      `
    }
  ];
  
  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridApi.setQuickFilter(filterValue);
  }
  onGridReady(params: any) {
    this.gridApi = params.api;
  }
  onCellClicked(event: any) {
    if (event.column.colId === 'actions') {
      const serviceId = event.data.name;
      if (event.event.target.classList.contains('edit-btn')) {
        this.openServiceDialog(event.data);
      } else if (event.event.target.classList.contains('delete-btn')) {
        this.deleteService(serviceId);
      }
    }
  }

  deleteService(serviceName: string) {
    if (confirm(`Are you sure you want to delete "${serviceName}"?`)) {
      this.service_mgmtSvc.deleteService([serviceName]).subscribe({
        next: () => {
          this.successMessage = 'Service deleted successfully';
          this.loadServices();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to delete service';
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }
  openServiceDialog(service?: service) {
    const dialogRef = this.dialog.open(ServiceFormComponent, {
      width: '90vw',  // Scales dynamically based on screen size
      maxWidth: '600px', // Limits max width for larger screens
      maxHeight: '90vh', // Prevents form from getting cut off on small screens
      data: service || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadServices();
    });
  }

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.service_mgmtSvc.getService().subscribe({
      next: data => {
        console.log('Services:',data);
        if (data && data.length > 0) {
          this.serviceList = data; // keep this if you're using rowData binding too
          if (this.gridApi) {

            this.gridApi.setRowData(this.serviceList); // ✅ this will 100% force UI refresh
          }          this.errorMessage = ''; // Clear any previous error
        } else {
          // Do nothing — or show a message if you want
          this.errorMessage = '';
        }
      },
      error: err => {
        this.errorMessage = 'Failed to load service.';
      }
    });
  }

}
