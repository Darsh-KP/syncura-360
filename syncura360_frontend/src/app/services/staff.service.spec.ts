import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { StaffService, Staff, StaffUpdateDto } from './staff.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('StaffService', () => {
  let service: StaffService;
  let httpMock: HttpTestingController;

  const mockStaff: Staff = {
    username: 'nurse1',
    role: 'Nurse',
    firstName: 'Alice',
    lastName: 'Johnson',
    email: 'alice@example.com',
    phone: '1234567890',
    addressLine1: '123 Main St',
    addressLine2: '',
    city: 'New York',
    state: 'NY',
    postal: '10001',
    country: 'USA',
    dateOfBirth: '1990-01-01'
  };

  beforeEach(() => {
    localStorage.setItem('token', 'test-token');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [StaffService]
    });

    service = TestBed.inject(StaffService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all staff', () => {
    const mockResponse = { message: 'Success', staffList: [mockStaff] };

    service.getAllStaff().subscribe(staffList => {
      expect(staffList.length).toBe(1);
      expect(staffList[0].username).toBe('nurse1');
    });

    const req = httpMock.expectOne('http://localhost:8080/staff/all');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockResponse);
  });

  it('should handle error when getAllStaff fails', fakeAsync(() => {
    spyOn(console, 'error');
    service.getAllStaff().subscribe({
      next: () => fail('Should have errored'),
      error: (error) => {
        expect(error.message).toContain('Http failure response');
      }
    });
  
    const req = httpMock.expectOne('http://localhost:8080/staff/all');
    req.flush(null, { status: 500, statusText: 'Server Error' });
    tick();
  }));  

  it('should create staff', () => {
    const mockResponse = { message: 'Created' };

    service.createStaff([mockStaff]).subscribe(response => {
      expect(response.message).toBe('Created');
    });

    const req = httpMock.expectOne('http://localhost:8080/staff');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ staff: [mockStaff] });
    req.flush(mockResponse);
  });

  it('should update staff', () => {
    const updates: StaffUpdateDto[] = [{
      username: 'nurse1',
      fields: { role: 'Senior Nurse' }
    }];

    const mockResponse = { message: 'Updated', staffUsernames: ['nurse1'] };

    service.updateStaff(updates).subscribe(response => {
      expect(response.message).toBe('Updated');
      expect(response.staffUsernames).toContain('nurse1');
    });

    const req = httpMock.expectOne('http://localhost:8080/staff/batch');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ updates });
    req.flush(mockResponse);
  });

  it('should delete staff', fakeAsync(() => {
    service.deleteStaff([1, 2]).subscribe(response => {
      expect(response == null).toBeTrue(); // Accepts null or undefined
    });
  
    const req = httpMock.expectOne('http://localhost:8080/staff');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.body).toEqual({ staffIds: [1, 2] });
    req.flush(null); // Correct void response
    tick();
  }));  
});