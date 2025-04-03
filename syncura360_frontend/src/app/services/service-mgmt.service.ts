import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError, tap } from 'rxjs';
import {response} from 'express';

export interface service {
  name?: string;
  category?: string;
  description?: string;
  cost?: number;
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
    const body = {
      name: '',
      category: ''
    };

    return this.http.post<{ message: string; services: service[] } | null>(
      `${this.baseUrl}`,
      body,
      {
        headers: this.getHeaders()
      }
    ).pipe(
      tap(response => console.log('API Response:', response)), // Log the full response
      map(response => response?.services || []),
      catchError(error => {
        console.error('Error fetching services:', error);
        return throwError(() => new Error(error.message || 'Failed to load services.'));
      })
    );
  }



  createService(services: service): Observable<{ message: string }> {
    const headers = this.getHeaders();
    tap(response => console.log('API Response:', response)); // Log the full response
    const base = 'http://localhost:8080/services/new';
    return this.http.post<{ message: string }>(base, services, { headers });
  }

  deleteService(services: service): Observable<{ message: string }> {
    const headers = this.getHeaders();
    return this.http.delete<{ message: string }>(this.baseUrl, {
      headers,
      body: services // ✅ send the full Service object in the body
    }).pipe(
      catchError(error => {
        console.error('Error deleting Service item:', error);
        return throwError(() => new Error(error.message || 'Failed to delete Service item.'));
      })
    );
  }


  updateServiceQuantities(services: service): Observable<{ message: string }> {
    const headers = this.getHeaders();
    return this.http.put<{ message: string }>(this.baseUrl, services, { headers });
  }
}
