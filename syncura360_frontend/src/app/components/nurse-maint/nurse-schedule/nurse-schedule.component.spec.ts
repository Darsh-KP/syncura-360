import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NurseScheduleComponent } from './nurse-schedule.component';
import { ScheduleService } from '../../../services/schedule.service';
import { of, throwError } from 'rxjs';
import { CalendarModule, CalendarEvent, DateAdapter } from 'angular-calendar';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';

describe('NurseScheduleComponent', () => {
  let component: NurseScheduleComponent;
  let fixture: ComponentFixture<NurseScheduleComponent>;
  let mockScheduleService: jasmine.SpyObj<ScheduleService>;

  const mockSchedules = [
    { username: 'nurse1', start: '2025-04-20T08:00:00', end: '2025-04-20T16:00:00', department: 'Cardiology' },
    { username: 'nurse1', start: '2025-04-21T10:00:00', end: '2025-04-21T18:00:00', department: 'Emergency' }
  ];

  beforeEach(async () => {
    mockScheduleService = jasmine.createSpyObj('ScheduleService', ['getMySchedule']);

    await TestBed.configureTestingModule({
      imports: [NurseScheduleComponent, CalendarModule.forRoot({ provide: DateAdapter, useFactory: () => adapterFactory() }), NoopAnimationsModule],
      providers: [
        { provide: ScheduleService, useValue: mockScheduleService }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(NurseScheduleComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch schedules on init', fakeAsync(() => {
    mockScheduleService.getMySchedule.and.returnValue(of({
      message: 'Fetched successfully',
      scheduledShifts: mockSchedules
    }));
    fixture.detectChanges(); // triggers ngOnInit
    tick();
  
    expect(mockScheduleService.getMySchedule).toHaveBeenCalled();
    expect(component.scheduleList.length).toBe(2);
    expect(component.events.length).toBe(2);
  }));
  

  it('should handle error on fetchSchedules', fakeAsync(() => {
    spyOn(console, 'error');
    mockScheduleService.getMySchedule.and.returnValue(throwError(() => new Error('API fail')));
    component.fetchSchedules();
    tick();
    expect(console.error).toHaveBeenCalledWith('Error fetching schedules:', jasmine.any(Error));
  }));

  it('should format date and time correctly', () => {
    const sampleDateTime = '2025-04-20T08:00:00';
    expect(component.formatDate(sampleDateTime)).toBe('2025-04-20');
    expect(component.formatTime(sampleDateTime)).toMatch(/08:00/);
  });

  it('should populate calendar events from scheduleList', () => {
    component.scheduleList = mockSchedules;
    component.populateCalendarEvents();
    expect(component.events.length).toBe(2);
    expect(component.events[0].title).toBe('Shift');
    expect(component.events[0].start instanceof Date).toBeTrue();
  });

  it('should navigate to previous week and fetch schedules', fakeAsync(() => {
    const initialDate = new Date(component.viewDate);
    spyOn(component, 'fetchSchedules');
    component.previousWeek();
    expect(component.viewDate.getTime()).toBeLessThan(initialDate.getTime());
    expect(component.fetchSchedules).toHaveBeenCalled();
  }));
  

  it('should navigate to next week and fetch schedules', fakeAsync(() => {
    const initialDate = new Date(component.viewDate);
    spyOn(component, 'fetchSchedules');
    component.nextWeek();
    expect(component.viewDate.getTime()).toBeGreaterThan(initialDate.getTime());
    expect(component.fetchSchedules).toHaveBeenCalled();
  }));  

  it('should set gridApi and size columns to fit on grid ready', () => {
    const mockApi = jasmine.createSpyObj('gridApi', ['sizeColumnsToFit']);
    component.onGridReady({ api: mockApi });
    expect(component.gridApi).toBe(mockApi);
    expect(mockApi.sizeColumnsToFit).toHaveBeenCalled();
  });

  it('should apply quick filter to grid', () => {
    const mockApi = jasmine.createSpyObj('gridApi', ['setQuickFilter']);
    component.gridApi = mockApi;
    const mockEvent = { target: { value: 'search' } };
    component.onQuickFilterChanged(mockEvent);
    expect(mockApi.setQuickFilter).toHaveBeenCalledWith('search');
  });
});
