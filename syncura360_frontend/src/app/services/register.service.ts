import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private apiUrl = 'http://localhost:8080/register/hospital';

  constructor(private http: HttpClient) {}

  registerHospital(data: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http.post<any>(this.apiUrl, data, { headers })
      .pipe(
        catchError(error => {
          console.error('Registration failed:', error);
          return throwError(error);
        })
      );
  }
}