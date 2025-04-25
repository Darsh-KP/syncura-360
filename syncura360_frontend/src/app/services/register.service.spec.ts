import { TestBed } from '@angular/core/testing';
import { RegisterService } from './register.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';

describe('RegisterService', () => {
  let service: RegisterService;
  let httpMock: HttpTestingController;

  const mockRegisterData = {
    hospital: {
      name: 'Test Hospital',
      addressLine1: '123 Test St',
      city: 'Testville',
      state: 'TS',
      postal: '12345',
      telephone: '1234567890',
      type: 'General',
      traumaLevel: 'Level I',
      hasHelipad: true
    },
    staff: {
      username: 'admin',
      passwordHash: 'password123',
      role: 'Super Admin',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      phone: '0987654321',
      addressLine1: '456 Admin St',
      city: 'Admin City',
      state: 'AD',
      postal: '54321',
      country: 'USA',
      dateOfBirth: '1980-01-01'
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RegisterService]
    });

    service = TestBed.inject(RegisterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should make a POST request to register a hospital', () => {
    const mockResponse = 'Registration successful';

    service.registerHospital(mockRegisterData).subscribe(response => {
      expect(response).toBe(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:8080/register/hospital');
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toBe('application/json');
    req.flush(mockResponse);
  });

  it('should handle registration error', () => {
    spyOn(console, 'error');

    service.registerHospital(mockRegisterData).subscribe({
      next: () => fail('expected error'),
      error: (err) => {
        expect(err.message).toBe('Http failure response for http://localhost:8080/register/hospital: 500 Server Error');
        expect(console.error).toHaveBeenCalled();
      }
    });

    const req = httpMock.expectOne('http://localhost:8080/register/hospital');
    req.flush('Server Error', { status: 500, statusText: 'Server Error' });
  });
});