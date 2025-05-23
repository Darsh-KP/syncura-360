import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { CalendarModule, CalendarEvent, CalendarView, DateAdapter, CalendarEventTimesChangedEvent } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { Subject } from 'rxjs';
import { ScheduleService, Schedule } from '../../services/schedule.service';
import { ScheduleDialogComponent } from '../schedule-dialog/schedule-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-scheduling',
  standalone: true,
  imports: [
    CommonModule,
    AgGridModule,
    ReactiveFormsModule,
    NavbarComponent,
    CalendarModule,
    MatDialogModule
  ],
  providers: [
    {
      provide: DateAdapter,
      useFactory: adapterFactory,
    },
  ],
  templateUrl: './scheduling.component.html',
  styleUrls: ['./scheduling.component.css'],
})
export class SchedulingComponent implements OnInit {
  scheduleList: Schedule[] = [];
  staffList: any[] = [];
  errorMessage = '';
  successMessage = '';
  gridApi: any;
  showScheduleForm = false;
  loading = false;

  // 📅 Calendar properties
  view: CalendarView = CalendarView.Week;
  viewDate: Date = new Date();
  activeDayIsOpen: boolean = false;
  refresh: Subject<void> = new Subject<void>();
  events: CalendarEvent[] = [];

  scheduleForm: FormGroup;

  columnDefs: ColDef[] = [
    { headerName: 'Doctor/Nurse', field: 'username', sortable: true, filter: true },
    {
      headerName: 'Date',
      field: 'start',
      sortable: true,
      filter: true,
      valueFormatter: (params) => this.formatDate(params.value) // Format Date only
    },
    {
      headerName: 'Start Time',
      field: 'start',
      sortable: true,
      filter: true,
      valueFormatter: (params) => this.formatTime(params.value) // Format Start Time
    },
    {
      headerName: 'End Time',
      field: 'end',
      sortable: true,
      filter: true,
      valueFormatter: (params) => this.formatTime(params.value) // Format End Time
    },
    { headerName: 'Department', field: 'department', sortable: true, filter: true },
    {
      headerName: 'Actions',
      cellRenderer: (params: any) => {
        return `
          <button class="edit-btn mr-6" data-action="edit">Edit</button>
          <button class="delete-btn" data-action="delete">Delete</button>
        `;
      },
      suppressSizeToFit: true,
      minWidth: 140
    }
  ];

  formatDate(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toISOString().split('T')[0]; // Extract YYYY-MM-DD
  }

  formatTime(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true }); // Format HH:MM AM/PM
  }


  constructor(private fb: FormBuilder, private scheduleService: ScheduleService, private dialog: MatDialog) {
    this.scheduleForm = this.fb.group({
      username: ['', Validators.required],
      date: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      department: ['']
    });
  }

  ngOnInit() {
    this.fetchSchedules();
    this.fetchStaff();
  }

  fetchSchedules() {
    const start = new Date();
    start.setHours(0, 0, 0, 0); // Start of today

    // Remove limitation by setting far future date
    const end = new Date('2100-01-01T23:59:59');

    const formattedStart = this.formatDateTime(start);
    const formattedEnd = this.formatDateTime(end);

    this.scheduleService.getSchedules(formattedStart, formattedEnd).subscribe({
      next: (response) => {
        this.scheduleList = response.scheduledShifts || [];
        this.populateCalendarEvents();
        if (this.gridApi) {
          this.gridApi.setRowData(this.scheduleList);
        }
      },
      error: (error) => this.errorMessage = error.message || 'Failed to load schedules.'
    });
  }


  formatDateTime(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }



  fetchStaff() {
    // Example static data for staff selection (replace with API call later)
    this.staffList = [
      { id: 1, name: 'Dr. Smith' },
      { id: 2, name: 'Nurse Jane' }
    ];
  }

  populateCalendarEvents() {
    this.events = this.scheduleList.map((schedule) => ({
      title: `Shift: ${schedule.username}`,
      start: new Date(schedule.start),
      end: new Date(schedule.end),
      color: { primary: '#1e90ff', secondary: '#D1E8FF' },
      draggable: true,
      meta: {
        originalStart: schedule.start,
        originalUsername: schedule.username,
        department: schedule.department
      }
    }));
    this.refresh.next();
  }


  setView(view: CalendarView) {
    this.view = view;
  }

  previousWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 7));
    this.fetchSchedules();
  }

  nextWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
    this.fetchSchedules();
  }

  createSchedule(newSchedule: any) {
    this.loading = true;
    const schedule: Schedule = {
      username: newSchedule.username,
      start: `${newSchedule.date}T${newSchedule.startTime}:00`,
      end: `${newSchedule.date}T${newSchedule.endTime}:00`,
      department: newSchedule.department
    };

    this.scheduleService.createSchedule([schedule]).subscribe({
      next: (response) => {
        this.successMessage = response.message;
        this.fetchSchedules();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to schedule.';
        this.loading = false;
      }
    });
  }

  deleteSchedule(schedule: Schedule) {
    this.scheduleService.deleteSchedule([{ start: schedule.start, username: schedule.username }]).subscribe({
      next: (response) => {
        this.successMessage = response.message;
        this.fetchSchedules();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to delete schedule.';
      }
    });
  }

  updateSchedule(schedule: Schedule, originalStart: string, originalUsername: string) {
    const updatePayload = [
      {
        id: {
          username: originalUsername,
          start: originalStart
        },
        updates: {
          username: schedule.username,
          start: schedule.start,
          end: schedule.end,
          department: schedule.department
        }
      }
    ];

    this.scheduleService.updateSchedule(updatePayload).subscribe({
      next: (response) => {
        this.successMessage = response.message;
        this.fetchSchedules();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to update schedule.';
      }
    });
  }



  handleEvent(action: string, event: CalendarEvent): void {
    const eventEnd = event.end ?? event.start;

    const schedule = this.scheduleList.find(s =>
      new Date(s.start).getTime() === new Date(event.start).getTime() &&
      new Date(s.end).getTime() === new Date(eventEnd).getTime() &&
      s.username === event.title.replace('Shift: ', '')
    );

    if (schedule) {
      this.openScheduleDialog(schedule);
    } else {
      console.warn('No matching schedule found for clicked event');
    }
  }

  eventTimesChanged({
    event,
    newStart,
    newEnd,
  }: CalendarEventTimesChangedEvent): void {
    const originalStart = event.meta?.originalStart;
    const originalUsername = event.meta?.originalUsername;

    if (!originalStart || !originalUsername) {
      console.warn('Missing original event metadata');
      return;
    }

    if (newStart < new Date()) {
      alert("You cannot move shifts to the past.");
      return;
    }

    const formatDate = (date: Date) => {
      return date.toISOString().split('.')[0];
    };

    const updatedSchedule: Schedule = {
      username: originalUsername,
      start: this.toLocalISOString(newStart),
      end: this.toLocalISOString(newEnd ?? newStart),
      department: event.meta?.department || ''
    };

    this.updateSchedule(updatedSchedule, originalStart, originalUsername);

    event.start = newStart;
    event.end = newEnd ?? newStart;
    this.refresh.next();
  }


  toggleScheduleForm() {
    this.showScheduleForm = !this.showScheduleForm;
  }

  onGridReady(params: any) {
    this.gridApi = params.api;
  }

  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridApi.setQuickFilter(filterValue);
  }

  onCellClicked(event: any) {
    const action = event.event.target?.getAttribute('data-action');
    const schedule: Schedule = event.data;

    if (action === 'edit') {
      this.openScheduleDialog(schedule);
    } else if (action === 'delete') {
      const confirmDelete = confirm(`Are you sure you want to delete this schedule for ${schedule.username}?`);
      if (confirmDelete) {
        this.deleteSchedule(schedule);
      }
    }
  }

  openScheduleDialog(schedule: Schedule | null = null) {
    const dialogRef = this.dialog.open(ScheduleDialogComponent, {
      autoFocus: false,
      restoreFocus: true,
      width: '500px',
      data: { schedule }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'updated' || result === 'deleted') {
        this.fetchSchedules();
      }
    });
  }

  private toLocalISOString = (date: Date): string => {
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}` +
           `T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  }

}
