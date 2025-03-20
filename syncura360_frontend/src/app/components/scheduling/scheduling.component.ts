import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { CalendarModule, CalendarEvent, CalendarView, DateAdapter, CalendarEventTimesChangedEvent } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-scheduling',
  standalone: true,
  imports: [
    CommonModule,
    AgGridModule,
    ReactiveFormsModule,
    NavbarComponent,
    CalendarModule,
  ],
  templateUrl: './scheduling.component.html',
  styleUrls: ['./scheduling.component.css'],
})
export class SchedulingComponent implements OnInit {
  scheduleList: any[] = [];
  staffList: any[] = [];
  errorMessage = '';
  successMessage = '';
  gridApi: any;
  showScheduleForm = false;
  loading = false;

  // 📅 Calendar properties
  view: CalendarView = CalendarView.Week; // Show the week view by default
  viewDate: Date = new Date(); // Current date
  activeDayIsOpen: boolean = false; // For opening event details
  refresh: Subject<void> = new Subject<void>(); // For updating calendar
  events: CalendarEvent[] = [];

  scheduleForm: FormGroup;

  columnDefs: ColDef[] = [
    { headerName: 'Doctor/Nurse', field: 'staffName', sortable: true, filter: true },
    { headerName: 'Date', field: 'date', sortable: true, filter: true },
    { headerName: 'Start Time', field: 'startTime', sortable: true, filter: true },
    { headerName: 'End Time', field: 'endTime', sortable: true, filter: true }
  ];

  constructor(private fb: FormBuilder) {
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

  setView(view: CalendarView) {
    this.view = view;
  }

  previousWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 7));
  }

  nextWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
  }

  addAppointment(event: any): void {
    const newEvent: CalendarEvent = {
      title: 'New Appointment',
      start: event.date,
      allDay: true,
      color: { primary: '#1e90ff', secondary: '#D1E8FF' },
      draggable: true,
    };
    this.events = [...this.events, newEvent];
    this.refresh.next();
  }

  handleEvent(action: string, event: CalendarEvent): void {
    console.log('Event clicked:', event);
    alert(`Event: ${event.title}`);
  }

  eventTimesChanged({
    event,
    newStart,
    newEnd,
  }: CalendarEventTimesChangedEvent): void {
    event.start = newStart;
    event.end = newEnd;
    this.refresh.next();
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
    setTimeout(() => {
      this.successMessage = 'Appointment Scheduled Successfully!';
      
      // Add to AG Grid list
      this.scheduleList.push(this.scheduleForm.value);
      
      // Add to Calendar View
      this.events.push({
        title: 'New Appointment',
        start: new Date(this.scheduleForm.value.date + 'T' + this.scheduleForm.value.startTime),
        end: new Date(this.scheduleForm.value.date + 'T' + this.scheduleForm.value.endTime),
        color: { primary: '#1e90ff', secondary: '#D1E8FF' },
        draggable: true
      });

      this.refresh.next();
      this.toggleScheduleForm();
      this.loading = false;
    }, 1000);
  }
}
