import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Staff {
  id?: number;
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
  id: number;
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
    return this.http.get<Staff[]>(`${this.baseUrl}/all`, { headers: this.getHeaders() });
  }

  createStaff(staff: Staff[]): Observable<{ message: string }> {
    const requestBody = { staff };
    const headers = this.getHeaders();
    
    console.log("Sending Token: ", headers.get('Authorization')); // Debugging
  
    return this.http.post<{ message: string }>(this.baseUrl, requestBody, { headers });
  }  

  updateStaff(updates: StaffUpdateDto[]): Observable<{ message: string; staffIds: number[] }> {
    const requestBody: StaffUpdateRequest = { updates };
    return this.http.put<{ message: string; staffIds: number[] }>(`${this.baseUrl}/batch`, requestBody, { headers: this.getHeaders() });
  }

  deleteStaff(staffIds: number[]): Observable<void> {
    return this.http.request<void>('delete', this.baseUrl, {
      headers: this.getHeaders(),
      body: { staffIds }
    });
  }
}