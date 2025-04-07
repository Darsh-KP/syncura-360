import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';

export interface Staff {
  username: string;
  passwordHash?: string;
  role: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  postal: string;
  country: string;
  dateOfBirth: string;
}

export interface StaffUpdateDto {
  username: string;
  fields: Partial<Staff>;
}

export interface StaffUpdateRequest {
  updates: StaffUpdateDto[];
}

@Injectable({
  providedIn: 'root'
})
export class StaffService {
  private baseUrl = 'http://localhost:8080/staff';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getAllStaff(): Observable<Staff[]> {
    return this.http.get<{ message: string; staffList: Staff[] }>(`${this.baseUrl}/all`, { headers: this.getHeaders() })
      .pipe(
        map(response => {
          if (response.staffList) {
            return response.staffList;
          } else {
            throw new Error(response.message || 'Failed to fetch staff.');
          }
        }),
        catchError(error => {
          console.error('Error fetching staff:', error);
          return throwError(() => new Error(error.message || 'Failed to load staff.'));
        })
      );
  }

  createStaff(staff: Staff[]): Observable<{ message: string }> {
    const requestBody = { staff };
    const headers = this.getHeaders();
      
    return this.http.post<{ message: string }>(this.baseUrl, requestBody, { headers });
  }  

  updateStaff(updates: StaffUpdateDto[]): Observable<{ message: string; staffUsernames: string[] }> {
    const requestBody: StaffUpdateRequest = { updates };
    return this.http.put<{ message: string; staffUsernames: string[] }>(`${this.baseUrl}/batch`, requestBody, { headers: this.getHeaders() });
  }

  deleteStaff(staffIds: number[]): Observable<void> {
    return this.http.request<void>('delete', this.baseUrl, {
      headers: this.getHeaders(),
      body: { staffIds }
    });
  }
}