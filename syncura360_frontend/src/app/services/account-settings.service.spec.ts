import { TestBed } from '@angular/core/testing';
import { AccountSettingsService, PasswordUpdateRequest, PasswordUpdateResponse, HospitalInfo, StaffInfo } from './account-settings.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('AccountSettingsService', () => {
  let service: AccountSettingsService;
  let httpMock: HttpTestingController;

  const mockToken = 'mock-token';

  beforeEach(() => {
    localStorage.setItem('token', mockToken);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccountSettingsService]
    });

    service = TestBed.inject(AccountSettingsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('token');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send PUT request to change password', () => {
    const mockRequest: PasswordUpdateRequest = { currentPassword: 'oldPass', newPassword: 'newPass' };
    const mockResponse: PasswordUpdateResponse = { message: 'Password updated successfully' };

    service.changePassword(mockRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/password`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    expect(req.request.body).toEqual(mockRequest);

    req.flush(mockResponse);
  });

  it('should send GET request to fetch hospital info', () => {
    const mockHospitalInfo: HospitalInfo = {
      hospitalName: 'Test Hospital',
      addressLine1: '123 Main St',
      addressLine2: '',
      city: 'Test City',
      state: 'TS',
      postal: '12345',
      telephone: '1234567890',
      type: 'General',
      traumaLevel: 'Level I',
      hasHelipad: true
    };

    service.getHospitalInfo().subscribe(info => {
      expect(info).toEqual(mockHospitalInfo);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/hospital`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);

    req.flush(mockHospitalInfo);
  });

  it('should send GET request to fetch staff info', () => {
    const mockStaffInfo: StaffInfo = {
      username: 'user1',
      role: 'Admin',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      dateOfBirth: '1990-01-01',
      phone: '1234567890',
      addressLine1: '456 Secondary St',
      addressLine2: '',
      city: 'Sample City',
      state: 'SC',
      postal: '54321',
      country: 'USA',
      specialty: 'Cardiology',
      yearsExperience: 5
    };

    service.getStaffInfo().subscribe(info => {
      expect(info).toEqual(mockStaffInfo);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/staff`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);

    req.flush(mockStaffInfo);
  });
});