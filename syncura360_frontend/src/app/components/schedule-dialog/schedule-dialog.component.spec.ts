import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ScheduleDialogComponent } from './schedule-dialog.component';
import { ScheduleService } from '../../services/schedule.service';
import { StaffService } from '../../services/staff.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';

describe('ScheduleDialogComponent - Create Mode', () => {
  let component: ScheduleDialogComponent;
  let fixture: ComponentFixture<ScheduleDialogComponent>;
  let mockScheduleService: jasmine.SpyObj<ScheduleService>;
  let mockStaffService: jasmine.SpyObj<StaffService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<ScheduleDialogComponent>>;

  beforeEach(async () => {
    mockScheduleService = jasmine.createSpyObj('ScheduleService', ['createSchedule', 'updateSchedule', 'deleteSchedule']);
    mockStaffService = jasmine.createSpyObj('StaffService', ['getAllStaff']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    mockStaffService.getAllStaff.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [
        ScheduleDialogComponent,
        CommonModule,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: ScheduleService, useValue: mockScheduleService },
        { provide: StaffService, useValue: mockStaffService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { schedule: null } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduleDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch usernames on init', () => {
    expect(mockStaffService.getAllStaff).toHaveBeenCalled();
    expect(component.usernamesList).toEqual([]);
  });

  it('should set errorMessage on staff fetch failure', () => {
    mockStaffService.getAllStaff.and.returnValue(throwError(() => new Error('fail')));
    component.ngOnInit();
    expect(component.errorMessage).toBe('fail');
  });

  it('should call createSchedule if no existing schedule', fakeAsync(() => {
    component.scheduleForm.setValue({
      username: 'john',
      date: new Date('2025-04-21'),
      startTime: '09:00',
      endTime: '17:00',
      department: 'Neurology'
    });

    mockScheduleService.createSchedule.and.returnValue(of({ message: 'Created!' }));
    component.saveSchedule();
    tick();

    expect(mockScheduleService.createSchedule).toHaveBeenCalled();
    expect(mockDialogRef.close).toHaveBeenCalledWith('updated');
  }));
});

describe('ScheduleDialogComponent - Edit Mode', () => {
  let component: ScheduleDialogComponent;
  let fixture: ComponentFixture<ScheduleDialogComponent>;
  let mockScheduleService: jasmine.SpyObj<ScheduleService>;
  let mockStaffService: jasmine.SpyObj<StaffService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<ScheduleDialogComponent>>;

  const dummySchedule = {
    username: 'john',
    start: '2025-04-20T08:00:00',
    end: '2025-04-20T16:00:00',
    department: 'Cardiology'
  };

  beforeEach(async () => {
    mockScheduleService = jasmine.createSpyObj('ScheduleService', ['createSchedule', 'updateSchedule', 'deleteSchedule']);
    mockStaffService = jasmine.createSpyObj('StaffService', ['getAllStaff']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    mockStaffService.getAllStaff.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [
        ScheduleDialogComponent,
        CommonModule,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: ScheduleService, useValue: mockScheduleService },
        { provide: StaffService, useValue: mockStaffService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { schedule: dummySchedule } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduleDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call updateSchedule if editing existing schedule', fakeAsync(() => {
    const date = new Date('2025-04-20T00:00:00'); // force consistent date
    component.scheduleForm.setValue({
      username: 'john',
      date: date,
      startTime: '08:00',
      endTime: '16:00',
      department: 'Cardiology'
    });
  
    // Log form errors if any
    expect(component.scheduleForm.valid).toBeTrue();
  
    mockScheduleService.updateSchedule.and.returnValue(of({ message: 'Updated!' }));
  
    component.saveSchedule();
    tick();
  
    expect(mockScheduleService.updateSchedule).toHaveBeenCalled();
    expect(mockDialogRef.close).toHaveBeenCalledWith('updated');
  }));
  

  it('should call deleteSchedule when confirmed', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    mockScheduleService.deleteSchedule.and.returnValue(of({ message: 'Deleted!' }));

    component.deleteSchedule();
    tick();

    expect(mockScheduleService.deleteSchedule).toHaveBeenCalled();
    expect(mockDialogRef.close).toHaveBeenCalledWith('deleted');
  }));

  it('should not delete schedule if confirmation is canceled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteSchedule();
    expect(mockScheduleService.deleteSchedule).not.toHaveBeenCalled();
  });

  it('should set error message if deleteSchedule fails', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    mockScheduleService.deleteSchedule.and.returnValue(throwError(() => new Error('Delete fail')));

    component.deleteSchedule();
    tick();

    expect(component.errorMessage).toBe('Delete fail');
  }));
});
