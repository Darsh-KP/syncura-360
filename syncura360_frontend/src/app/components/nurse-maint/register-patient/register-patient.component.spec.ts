import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterPatientComponent } from './register-patient.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

describe('RegisterPatientComponent', () => {
  let component: RegisterPatientComponent;
  let fixture: ComponentFixture<RegisterPatientComponent>;
  let httpMock: HttpTestingController;
  let mockRouter = { navigate: jasmine.createSpy('navigate') };
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ReactiveFormsModule, FormsModule, MatSnackBarModule],
      declarations: [RegisterPatientComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterPatientComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('T01: should submit form when all fields are valid', fakeAsync(() => {
    component.patientForm.setValue({
      firstName: 'Jane',
      lastName: 'Doe',
      dateOfBirth: '01/01/1990',
      gender: 'Female',
      bloodType: 'O+',
      phone: '1234567890',
      height: 65,
      weight: 120,
      addressLine1: '123 Main St',
      addressLine2: '',
      city: 'Testville',
      state: 'NY',
      postal: '12345',
      country: 'USA',
      emergencyContactName: 'John Doe',
      emergencyContactPhone: '0987654321'
    });

    component.submitPatient();
    const req = httpMock.expectOne('http://localhost:8080/patient');
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Patient registered' });

    expect(mockSnackBar.open).toHaveBeenCalledWith('Patient added successfully!', 'Close', {
      duration: 3000,
    });
  }));

  it('T02: should not submit form if required fields are missing', () => {
    component.patientForm.patchValue({
      firstName: '', // Required field left blank
    });

    component.onSubmit();

    expect(component.patientForm.invalid).toBeTrue();
    expect(mockSnackBar.open).not.toHaveBeenCalled();
  });

  it('T03: should toggle dark mode', () => {
    const initial = component.darkMode;
    component.toggleDarkMode();
    expect(component.darkMode).toBe(!initial);
  });

  it('T04: should handle server error when submitting form', fakeAsync(() => {
    component.patientForm.setValue({
      firstName: 'Error',
      lastName: 'Case',
      dateOfBirth: '02/02/1990',
      gender: 'Male',
      bloodType: 'A-',
      phone: '1111111111',
      height: 70,
      weight: 150,
      addressLine1: '404 Fail St',
      addressLine2: '',
      city: 'Errortown',
      state: 'CA',
      postal: '99999',
      country: 'USA',
      emergencyContactName: 'Fail Guy',
      emergencyContactPhone: '0000000000'
    });

    component.submitPatient();
    const req = httpMock.expectOne('http://localhost:8080/patient');
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Internal Server Error' }, { status: 500, statusText: 'Internal Server Error' });

    expect(mockSnackBar.open).toHaveBeenCalledWith('Error adding patient', 'Close', {
      duration: 3000,
    });
  }));
});
