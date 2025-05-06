import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SchedulingComponent } from './scheduling.component';
import { ScheduleService } from '../../services/schedule.service';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CalendarEvent, CalendarEventTimesChangedEventType } from 'angular-calendar';


describe('SchedulingComponent', () => {
  let component: SchedulingComponent;
  let fixture: ComponentFixture<SchedulingComponent>;
  let mockScheduleService: jasmine.SpyObj<ScheduleService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  const mockSchedules = [
    {
      username: 'john',
      start: '2025-04-20T08:00:00',
      end: '2025-04-20T16:00:00',
      department: 'Cardiology'
    }
  ];

  beforeEach(async () => {
    mockScheduleService = jasmine.createSpyObj('ScheduleService', [
      'getSchedules',
      'createSchedule',
      'updateSchedule',
      'deleteSchedule'
    ]);

    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [SchedulingComponent, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: ScheduleService, useValue: mockScheduleService },
        { provide: MatDialog, useValue: mockDialog }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SchedulingComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch schedules and populate data on init', fakeAsync(() => {
    mockScheduleService.getSchedules.and.returnValue(of({ message: 'Success', scheduledShifts: mockSchedules }));

    component.ngOnInit();
    tick();

    expect(mockScheduleService.getSchedules).toHaveBeenCalled();
    expect(component.scheduleList.length).toBe(1);
    expect(component.events.length).toBe(1);
  }));

  it('should handle schedule creation success', fakeAsync(() => {
    const form = component.scheduleForm;
    form.setValue({
      username: 'john',
      date: '2025-04-22',
      startTime: '08:00',
      endTime: '16:00',
      department: 'ICU'
    });

    mockScheduleService.createSchedule.and.returnValue(of({ message: 'Scheduled!' }));
    component.createSchedule(form.value);
    tick();

    expect(mockScheduleService.createSchedule).toHaveBeenCalled();
    expect(component.successMessage).toBe('Scheduled!');
  }));

  it('should handle schedule creation error', fakeAsync(() => {
    mockScheduleService.createSchedule.and.returnValue(throwError(() => new Error('Failed')));
    component.createSchedule({
      username: 'jane',
      date: '2025-04-22',
      startTime: '08:00',
      endTime: '16:00',
      department: 'ICU'
    });
    tick();

    expect(component.errorMessage).toBe('Failed');
  }));

  it('should delete schedule when confirmed', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    mockScheduleService.deleteSchedule.and.returnValue(of({ message: 'Deleted' }));

    component.deleteSchedule(mockSchedules[0]);
    tick();

    expect(mockScheduleService.deleteSchedule).toHaveBeenCalled();
    expect(component.successMessage).toBe('Deleted');
  }));

  it('should update schedule with new event data', fakeAsync(() => {
    const updatedSchedule = {
      username: 'john',
      start: '2025-04-21T09:00:00',
      end: '2025-04-21T17:00:00',
      department: 'Surgery'
    };

    mockScheduleService.updateSchedule.and.returnValue(of({ message: 'Updated' }));

    component.updateSchedule(updatedSchedule, mockSchedules[0].start, mockSchedules[0].username);
    tick();

    expect(mockScheduleService.updateSchedule).toHaveBeenCalled();
    expect(component.successMessage).toBe('Updated');
  }));

  it('should handle event time changes and update backend', fakeAsync(() => {
    const event: CalendarEvent = {
      title: 'Shift: john',
      start: new Date('2025-04-20T08:00:00'),
      end: new Date('2025-04-20T16:00:00'),
      meta: {
        originalStart: '2025-04-20T08:00:00',
        originalUsername: 'john',
        department: 'ER'
      }
    };

    const newStart = new Date('2025-04-22T10:00:00');
    const newEnd = new Date('2025-04-22T18:00:00');

    mockScheduleService.updateSchedule.and.returnValue(of({ message: 'Shift moved' }));

    component.eventTimesChanged({
      event,
      newStart,
      newEnd,
      type: CalendarEventTimesChangedEventType.Drop
    });
        tick();

    expect(mockScheduleService.updateSchedule).toHaveBeenCalled();
  }));
});
