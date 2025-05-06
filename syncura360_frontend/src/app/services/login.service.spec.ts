import { TestBed } from '@angular/core/testing';
import { LoginService } from './login.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

describe('LoginService', () => {
  let service: LoginService;
  let httpMock: HttpTestingController;

  const mockResponse = {
    message: 'Login successful',
    role: 'Admin'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoginService]
    });
    service = TestBed.inject(LoginService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear(); // Clear localStorage before each test
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no outstanding requests
    localStorage.clear(); // Clear after each test too
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store token and role on successful login', () => {
    const username = 'testuser';
    const password = 'testpass';
    const token = 'mock-token';

    service.login(username, password).subscribe(response => {
      expect(response.body).toEqual(mockResponse);
      expect(localStorage.getItem('token')).toBe(token);
      expect(localStorage.getItem('role')).toBe(mockResponse.role);
    });

    const req = httpMock.expectOne('http://localhost:8080/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username, password });

    // Simulate headers with Authorization
    const mockHeaders = new Headers();
    const httpResponse = new HttpResponse({
      body: mockResponse,
      headers: req.request.headers.append('Authorization', `Bearer ${token}`),
      status: 200
    });

    req.flush(mockResponse, { headers: { Authorization: `Bearer ${token}` } });
  });

  it('should handle missing Authorization header', () => {
    const username = 'testuser';
    const password = 'testpass';
    spyOn(console, 'warn');

    service.login(username, password).subscribe(response => {
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('role')).toBe(mockResponse.role);
      expect(console.warn).toHaveBeenCalledWith('Authorization header missing or improperly formatted');
    });

    const req = httpMock.expectOne('http://localhost:8080/login');
    req.flush(mockResponse, { headers: {} });
  });

  it('should handle login error', () => {
    const username = 'testuser';
    const password = 'wrongpass';
    spyOn(console, 'error');
  
    service.login(username, password).subscribe({
      next: () => fail('should have failed with error'),
      error: (err) => {
        expect(err.message).toBe('[object Object]');
        expect(console.error).toHaveBeenCalled();
      }
    });
  
    const req = httpMock.expectOne('http://localhost:8080/login');
    req.flush({ error: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
  });
  

  it('should remove token and role on logout', () => {
    localStorage.setItem('token', 'dummy-token');
    localStorage.setItem('role', 'User');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
  });

  it('should return true if authenticated', () => {
    localStorage.setItem('token', 'dummy-token');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should return false if not authenticated', () => {
    localStorage.removeItem('token');
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should return token with Bearer prefix', () => {
    localStorage.setItem('token', 'dummy-token');
    expect(service.getToken()).toBe('Bearer dummy-token');
  });

  it('should return null if no token', () => {
    localStorage.removeItem('token');
    expect(service.getToken()).toBeNull();
  });
});