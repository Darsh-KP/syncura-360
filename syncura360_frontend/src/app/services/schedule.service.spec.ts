import { TestBed } from '@angular/core/testing';
import { ScheduleService, Schedule } from './schedule.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('ScheduleService', () => {
  let service: ScheduleService;
  let httpMock: HttpTestingController;

  const mockSchedule: Schedule = {
    username: 'nurse1',
    start: '2025-04-20T08:00:00',
    end: '2025-04-20T16:00:00',
    department: 'Cardiology'
  };

  beforeEach(() => {
    localStorage.setItem('token', 'test-token');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ScheduleService]
    });

    service = TestBed.inject(ScheduleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get schedules with optional filters', () => {
    const mockResponse = { message: 'Fetched', scheduledShifts: [mockSchedule] };
    const start = '2025-04-20T00:00:00';
    const end = '2025-04-21T00:00:00';

    service.getSchedules(start, end, 'nurse1', 'Cardiology').subscribe(response => {
      expect(response.scheduledShifts?.length).toBe(1);
      expect(response.message).toBe('Fetched');
    });

    const req = httpMock.expectOne('http://localhost:8080/schedule');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ start, end, username: 'nurse1', department: 'Cardiology' });
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockResponse);
  });

  it('should create new schedules', () => {
    const mockResponse = { message: 'Created' };

    service.createSchedule([mockSchedule]).subscribe(response => {
      expect(response.message).toBe('Created');
    });

    const req = httpMock.expectOne('http://localhost:8080/schedule/new');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ shifts: [mockSchedule] });
    req.flush(mockResponse);
  });

  it('should update schedules', () => {
    const updates = [{
      id: { username: 'nurse1', start: '2025-04-20T08:00:00' },
      updates: { end: '2025-04-20T17:00:00' }
    }];
    const mockResponse = { message: 'Updated' };

    service.updateSchedule(updates).subscribe(response => {
      expect(response.message).toBe('Updated');
    });

    const req = httpMock.expectOne('http://localhost:8080/schedule');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ updates });
    req.flush(mockResponse);
  });

  it('should delete schedules', () => {
    const shiftsToDelete = [{ start: '2025-04-20T08:00:00', username: 'nurse1' }];
    const mockResponse = { message: 'Deleted' };

    service.deleteSchedule(shiftsToDelete).subscribe(response => {
      expect(response.message).toBe('Deleted');
    });

    const req = httpMock.expectOne('http://localhost:8080/schedule');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.body).toEqual({ shifts: shiftsToDelete });
    req.flush(mockResponse);
  });

  it('should get current user schedule', () => {
    const start = '2025-04-20T00:00:00';
    const end = '2025-04-21T00:00:00';
    const mockResponse = { message: 'Fetched', scheduledShifts: [mockSchedule] };

    service.getMySchedule(start, end).subscribe(response => {
      expect(response.message).toBe('Fetched');
      expect(response.scheduledShifts?.length).toBe(1);
    });

    const req = httpMock.expectOne('http://localhost:8080/schedule/staff');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ start, end });
    req.flush(mockResponse);
  });
});
