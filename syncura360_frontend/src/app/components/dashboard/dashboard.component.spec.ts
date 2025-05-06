import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { StaffService, Staff } from '../../services/staff.service';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { fakeAsync, tick } from '@angular/core/testing';


describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockStaffService: jasmine.SpyObj<StaffService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockStaffService = jasmine.createSpyObj('StaffService', ['getAllStaff', 'deleteStaff']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, DashboardComponent],
      providers: [
        { provide: StaffService, useValue: mockStaffService },
        { provide: MatDialog, useValue: mockDialog },
        { provide: Router, useValue: mockRouter }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch staff on init', () => {
    const dummyStaff: Staff[] = [
      { firstName: 'John', lastName: 'Doe', username: '', role: 'Doctor', email: '', phone: '', addressLine1: '', addressLine2: '', city: '', state: '', postal: '', country: '', dateOfBirth: '' }
    ];
    mockStaffService.getAllStaff.and.returnValue(of(dummyStaff));

    component.ngOnInit();

    expect(mockStaffService.getAllStaff).toHaveBeenCalled();
    expect(component.staffList[0].firstName).toBe('John');
    expect(component.staffList[0].lastName).toBe('Doe');

  });

  it('should handle error on staff fetch', () => {
    mockStaffService.getAllStaff.and.returnValue(throwError(() => new Error('API fail')));

    component.fetchStaff();

    expect(component.errorMessage).toBe('API fail');
  });

  it('should set gridApi on grid ready', () => {
    const mockApi = { setRowData: jasmine.createSpy() };
    component.onGridReady({ api: mockApi });
    expect(component.gridApi).toBe(mockApi);
  });

  it('should set quick filter on input change', () => {
    const mockApi = { setQuickFilter: jasmine.createSpy() };
    component.gridApi = mockApi;

    const event = { target: { value: 'test' } };
    component.onQuickFilterChanged(event);

    expect(mockApi.setQuickFilter).toHaveBeenCalledWith('test');
  });

  it('should call openStaffDialog when edit is clicked', () => {
    spyOn(component, 'openStaffDialog');


    const mockStaff: Staff = {
      username: 'jdoe',
      role: 'Doctor',
      firstName: 'John',
      lastName: 'Doe',
      email: 'jdoe@example.com',
      phone: '1234567890',
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postal: '',
      country: '',
      dateOfBirth: ''
    };

    const event = {
      column: { colId: 'actions' },
      data: mockStaff,
      event: {
        target: { classList: { contains: (cls: string) => cls === 'edit-btn' } }
      }
    };


    component.onCellClicked(event);
    expect(component.openStaffDialog).toHaveBeenCalledWith(mockStaff);
  });

  it('should call deleteStaff when delete is confirmed', (done) => {
    spyOn(window, 'confirm').and.returnValue(true);
    mockStaffService.deleteStaff.and.returnValue(of());
  
    spyOn(component, 'fetchStaff').and.callFake(() => {
      expect(mockStaffService.deleteStaff).toHaveBeenCalledWith([1]);
      expect(component.successMessage).toBe('Staff member removed successfully.');
      expect(component.fetchStaff).toHaveBeenCalled();
      done(); 
    });
  
    component.deleteStaff(1);
  });  
  
  
  it('should not delete staff when user cancels confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteStaff(1);
    expect(mockStaffService.deleteStaff).not.toHaveBeenCalled();
  });
});
