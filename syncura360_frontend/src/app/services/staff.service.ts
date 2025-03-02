import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Staff {
  username: string;
  passwordHash: string;
  role: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  addressLine1: string;
  city: string;
  state: string;
  postal: string;
  country: string;
  dateOfBirth: string;
}

@Injectable({
  providedIn: 'root'
})
export class StaffService {
  private baseUrl = 'http://localhost:8080/staff';

  constructor(private http: HttpClient) {}

  getAllStaff(): Observable<Staff[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });

    return this.http.get<Staff[]>(`${this.baseUrl}/all`, { headers });
  }

  createStaff(staff: Staff): Observable<{ message: string }> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });

    return this.http.post<{ message: string }>(this.baseUrl, staff, { headers });
  }

  deleteStaff(staffIds: number[]): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });

    return this.http.request<void>('delete', this.baseUrl, { 
      headers,
      body: { staffIds }
    });
  }
}
