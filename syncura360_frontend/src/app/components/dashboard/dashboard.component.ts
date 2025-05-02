import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { StaffService, Staff } from '../../services/staff.service';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { UserMaintComponent } from '../user-maint/user-maint.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    AgGridModule,
    NavbarComponent
],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  staffList: Staff[] = [];
  errorMessage = '';

  successMessage = '';
  gridApi: any;
  public themeClass: string = 'ag-theme-quartz';

  columnDefs: ColDef[] = [
    { headerName: 'Name', field: 'fullName', sortable: true, filter: true, flex: 1 },
    { headerName: 'Email', field: 'email', sortable: true, filter: true, flex: 1 },
    { headerName: 'Phone', field: 'phone', sortable: true, filter: true, flex: 1 },
    { headerName: 'Role', field: 'role', sortable: true, filter: true, flex: 1 },
    {
      headerName: 'Actions',
      field: 'actions',
      cellRenderer: (params: any) => `
        <button class="edit-btn bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600" data-id="${params.data.id}">
          Edit
        </button>
      `,
      flex: 1,
      suppressSizeToFit: false
    }
  ];  
  

  constructor(
    private staffService: StaffService,
    private dialog: MatDialog,
    private router: Router  // Inject Router
  ) {}

  ngOnInit() {
    this.fetchStaff();
  }

  fetchStaff() {
    this.staffService.getAllStaff().subscribe({
      next: (staff) => {
        this.staffList = staff.map(s => ({
          ...s,
          fullName: `${s.firstName} ${s.lastName}`
        }));
        if (this.gridApi) {
          this.gridApi.setRowData(this.staffList);
        }
      },
      error: (error) => this.errorMessage = error.message || 'Failed to load staff.'
    });
  }

  onGridReady(params: any) {
    this.gridApi = params.api;
  }

  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridApi.setQuickFilter(filterValue);
  }

  openStaffDialog(staff?: Staff) {
    const dialogRef = this.dialog.open(UserMaintComponent, {
      width: '90vw',  // Scales dynamically based on screen size
      maxWidth: '600px', // Limits max width for larger screens
      maxHeight: '90vh', // Prevents form from getting cut off on small screens
      data: staff || null
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.fetchStaff();
    });
  }
  

  onCellClicked(event: any) {
    if (event.column.colId === 'actions') {
      const staffId = event.data.id;
      if (event.event.target.classList.contains('edit-btn')) {
        this.openStaffDialog(event.data);
      } else if (event.event.target.classList.contains('delete-btn')) {
        this.deleteStaff(staffId);
      }
    }
  }

  deleteStaff(staffId: number) {
    if (!confirm('Are you sure you want to delete this staff member?')) return;

    this.staffService.deleteStaff([staffId]).subscribe({
      next: () => {
        this.successMessage = 'Staff member removed successfully.';
        this.fetchStaff();
      },
      error: (error) => this.errorMessage = error.message || 'Failed to delete staff.'
    });
  }

}
