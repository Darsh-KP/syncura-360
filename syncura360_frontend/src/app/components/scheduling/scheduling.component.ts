import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-scheduling',
  standalone: true,
  imports: [CommonModule, AgGridModule, ReactiveFormsModule, NavbarComponent], 
  templateUrl: './scheduling.component.html',
  styleUrls: ['./scheduling.component.css']
})
export class SchedulingComponent implements OnInit {
  scheduleList: any[] = [];
  staffList: any[] = [];
  errorMessage = '';
  successMessage = '';
  gridApi: any;
  showScheduleForm = false;
  loading = false;

  scheduleForm: FormGroup;

  columnDefs: ColDef[] = [
    { headerName: 'Doctor/Nurse', field: 'staffName', sortable: true, filter: true },
    { headerName: 'Date', field: 'date', sortable: true, filter: true },
    { headerName: 'Start Time', field: 'startTime', sortable: true, filter: true },
    { headerName: 'End Time', field: 'endTime', sortable: true, filter: true }
  ];

  constructor(private fb: FormBuilder) { // Removed service for now
    this.scheduleForm = this.fb.group({
      staffId: ['', Validators.required],
      date: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.fetchStaff();
  }

  fetchStaff() {
    // Example static data for staff selection (replace with API call later)
    this.staffList = [
      { id: 1, name: 'Dr. Smith' },
      { id: 2, name: 'Nurse Jane' }
    ];
  }

  onCellClicked(event: any) {
    console.log('Cell clicked:', event);
    alert(`You clicked on: ${event.value}`);
  }
  

  onGridReady(params: any) {
    this.gridApi = params.api;
  }

  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridApi.setQuickFilter(filterValue);
  }

  toggleScheduleForm() {
    this.showScheduleForm = !this.showScheduleForm;
  }

  scheduleAppointment() {
    if (this.scheduleForm.invalid) return;

    this.loading = true;
    setTimeout(() => { // Mock delay for scheduling
      this.successMessage = 'Appointment Scheduled Successfully!';
      this.scheduleList.push(this.scheduleForm.value);
      this.toggleScheduleForm();
      this.loading = false;
    }, 1000);
  }
}
