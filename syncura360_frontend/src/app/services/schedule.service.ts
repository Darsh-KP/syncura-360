import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Schedule {
  start: string;
  end: string;
  username: string;
  department?: string; // Optional
}

export interface ScheduleResponse {
  message: string;
  scheduledShifts?: Schedule[];
}

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private baseUrl = 'http://localhost:8080/schedule';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  /**
   * Fetch all scheduled shifts based on filters (time frame, username, department)
   * @param start Start date (required)
   * @param end End date (required)
   * @param username Username (optional)
   * @param department Department (optional)
   * @returns Observable of ScheduleResponse
   */
  getSchedules(start: string, end: string, username?: string, department?: string): Observable<ScheduleResponse> {
    const requestBody = { start, end, username, department };
    return this.http.post<ScheduleResponse>(`${this.baseUrl}`, requestBody, { headers: this.getHeaders() });
  }

  /**
   * Create new scheduled shifts
   * @param shifts Array of Schedule objects
   * @returns Observable of response message
   */
  createSchedule(shifts: Schedule[]): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/new`, { shifts }, { headers: this.getHeaders() });
  }

  /**
   * Update scheduled shifts
   * @param updates Array of update objects, each containing old shift id and new data
   * @returns Observable of response message
   */
  updateSchedule(updates: { 
    id: { username: string; start: string }; 
    updates: { username?: string; start?: string; end?: string; department?: string } 
  }[]): Observable<{ message: string }> {
    const payload = { updates };
    return this.http.put<{ message: string }>(`${this.baseUrl}`, payload, {
      headers: this.getHeaders()
    });
  }


  /**
   * Delete scheduled shifts
   * @param shifts Array of objects with required `start` and `username` fields
   * @returns Observable of response message
   */
  deleteSchedule(shifts: { start: string; username: string }[]): Observable<{ message: string }> {
    return this.http.request<{ message: string }>('delete', `${this.baseUrl}`, {
      headers: this.getHeaders(),
      body: { shifts }
    });
  }

    /**
   * Fetch schedule for the current staff member (based on JWT)
   * @param start Start date string (ISO format)
   * @param end End date string (ISO format)
   * @returns Observable of ScheduleResponse
   */
  getMySchedule(start: string, end: string): Observable<ScheduleResponse> {
    const payload = { start, end };
    return this.http.post<ScheduleResponse>(`${this.baseUrl}/staff`, payload, {
      headers: this.getHeaders()
    });
  }

}