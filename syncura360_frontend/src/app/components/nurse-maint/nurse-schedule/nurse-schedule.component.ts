import { Component, OnInit } from '@angular/core';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../navbar/navbar.component';
import { CalendarModule, CalendarEvent, CalendarView, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { Subject } from 'rxjs';
import { ScheduleService, Schedule } from '../../../services/schedule.service';

@Component({
  selector: 'app-nurse-schedule',
  standalone: true,
  imports: [
    CommonModule,
    AgGridModule,
    NavbarComponent,
    CalendarModule,
  ],
  providers: [
    {
      provide: DateAdapter,
      useFactory: adapterFactory,
    },
  ],
  templateUrl: './nurse-schedule.component.html',
  styleUrls: ['./nurse-schedule.component.css'],
})
export class NurseScheduleComponent implements OnInit {
  scheduleList: Schedule[] = [];
  gridApi: any;

  // 📅 Calendar properties
  view: CalendarView = CalendarView.Week;
  viewDate: Date = new Date();
  refresh: Subject<void> = new Subject<void>();
  events: CalendarEvent[] = [];

  columnDefs: ColDef[] = [
    { headerName: 'Date', field: 'start', sortable: true, filter: true, valueFormatter: (params) => this.formatDate(params.value) },
    { headerName: 'Start Time', field: 'start', sortable: true, filter: true, valueFormatter: (params) => this.formatTime(params.value) },
    { headerName: 'End Time', field: 'end', sortable: true, filter: true, valueFormatter: (params) => this.formatTime(params.value) },
    { headerName: 'Department', field: 'department', sortable: true, filter: true },
  ];

  constructor(private scheduleService: ScheduleService) {}

  ngOnInit() {
    this.fetchSchedules();
  }

  fetchSchedules() {
    const start = new Date();
    start.setHours(0, 0, 0, 0);

    const end = new Date();
    end.setDate(end.getDate() + 7);
    end.setHours(23, 59, 59, 999);

    const formattedStart = this.formatDateTime(start);
    const formattedEnd = this.formatDateTime(end);

    // this.scheduleService.getNurseSchedules(formattedStart, formattedEnd).subscribe({
    //   next: (response) => {
    //     this.scheduleList = response.scheduledShifts || [];
    //     this.populateCalendarEvents();
    //     if (this.gridApi) {
    //       this.gridApi.setRowData(this.scheduleList);
    //     }
    //   },
    //   error: (error) => console.error(error)
    // });
  }

  formatDateTime(date: Date): string {
    return date.toISOString().split('.')[0];
  }

  formatDate(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toISOString().split('T')[0];
  }

  formatTime(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
  }

  populateCalendarEvents() {
    this.events = this.scheduleList.map((schedule) => ({
      title: `Shift`,
      start: new Date(schedule.start),
      end: new Date(schedule.end),
      color: { primary: '#1e90ff', secondary: '#D1E8FF' },
      draggable: false,
      meta: { department: schedule.department }
    }));
    this.refresh.next();
  }

  previousWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 7));
    this.fetchSchedules();
  }

  nextWeek() {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
    this.fetchSchedules();
  }

  onGridReady(params: any) {
    this.gridApi = params.api;
  }

  onQuickFilterChanged(event: any) {
    const filterValue = event.target.value;
    this.gridApi.setQuickFilter(filterValue);
  }
}
