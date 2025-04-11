import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PasswordUpdateRequest {
  username: string;
  currentPassword: string;
  newPassword: string;
}

export interface PasswordUpdateResponse {
  message: string;
}

export interface HospitalInfo {
  hospitalName: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  postal: string;
  telephone: string;
  type: string;
  traumaLevel: string;
  hasHelipad: boolean;
}

export interface StaffInfo {
  username: string;
  role: string;
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  phone: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  postal: string;
  country: string;
  specialty: string;
  years_experience: number;
}

@Injectable({
  providedIn: 'root'
})
export class AccountSettingsService {
  private baseUrl = 'http://localhost:8080/setting';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  changePassword(payload: PasswordUpdateRequest): Observable<PasswordUpdateResponse> {
    return this.http.put<PasswordUpdateResponse>(`${this.baseUrl}/password`, payload, {
      headers: this.getHeaders()
    });
  }

  getHospitalInfo(): Observable<HospitalInfo> {
    return this.http.get<HospitalInfo>(`${this.baseUrl}/hospital`, {
      headers: this.getHeaders()
    });
  }
  
  getStaffInfo(): Observable<StaffInfo> {
    return this.http.get<StaffInfo>(`${this.baseUrl}/staff`, {
      headers: this.getHeaders()
    });
  }
}
