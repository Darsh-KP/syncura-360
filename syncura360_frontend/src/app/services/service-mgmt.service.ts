import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError, tap } from 'rxjs';
import {response} from 'express';

export interface service {
  name: string;
  category: string;
  description: string;
  cost: number;
}

export interface serviceUpdateDto {
  name?: string;
  category?: string;
  description?: string;
  cost?: number;
}

export interface serviceUpdateRequest {
  updates: serviceUpdateDto[];
}

@Injectable({
  providedIn: 'root'
})

export class ServiceMgmtService {
  private baseUrl = 'http://localhost:8080/services';

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getService(): Observable<service[]> {
    // Remove the filtering or make it optional
    return this.http.post<{ message: string; services: service[] }>(
      `${this.baseUrl}`,
      {}, // Empty body to get all services
      {
        headers: this.getHeaders()
      }
    ).pipe(
      map(response => response?.services || []),
      catchError(error => {
        console.error('Error fetching services:', error);
        return throwError(() => new Error(error.message || 'Failed to load services.'));
      })
    );
  }


  createService(services: service[]): Observable<{ message: string }> {
    const headers = this.getHeaders();
    const body = { services }; // Wrap in services property
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/new`,
      body,
      { headers }
    ).pipe(
      tap(response => console.log('API Response:', response)),
      catchError(error => {
        console.error('Error creating service:', error);
        return throwError(() => error);
      })
    );
  }

  deleteService(serviceNames: string[]): Observable<{ message: string }> {
    const headers = this.getHeaders();
    const body = { names: serviceNames }; // Match backend's DeleteServicesDTO

    return this.http.delete<{ message: string }>(this.baseUrl, {
      headers,
      body, // ✅ Correct body format: { names: ["service1", "service2"] }
    }).pipe(
      catchError(error => {
        console.error('Error deleting services:', error);
        return throwError(() => new Error(error.message || 'Failed to delete services.'));
      })
    );
  }


  // Corrected update service method
  updateServices(updates: { name: string, updates: serviceUpdateDto }[]): Observable<{ message: string }> {
    const headers = this.getHeaders();
    const body: serviceUpdateRequest = { updates }; // This matches the backend's UpdateServicesRequest structure
    return this.http.put<{ message: string }>(
      `${this.baseUrl}/update`,
      body,
      { headers }
    ).pipe(
      catchError(error => {
        console.error('Error updating services:', error);
        return throwError(() => error);
      })
    );
  }
}
