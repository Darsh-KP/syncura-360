import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

interface LoginResponse {
  message: string;
  role: string;
  firstName: string;
  lastName: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private loginUrl = 'http://localhost:8080/login';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<HttpResponse<LoginResponse>> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http.post<LoginResponse>(this.loginUrl, { username, password }, { headers, observe: 'response' }).pipe(
      tap(response => {
        const authHeader = response.headers.get('Authorization');  
        const responseBody: LoginResponse | null = response.body;

        if (authHeader) {
          localStorage.setItem('token', authHeader);
        }

        if (responseBody) { 
          localStorage.setItem('role', responseBody.role);
          localStorage.setItem('firstName', responseBody.firstName);
          localStorage.setItem('lastName', responseBody.lastName);
        }
      }),
      catchError(error => {
        return throwError(() => new Error(error.error || 'Login failed. Please try again.'));
      })
    );    
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    return token ? `Bearer ${token}` : null;
  }

  getUserFullName(): string {
    const firstName = localStorage.getItem('firstName') || '';
    const lastName = localStorage.getItem('lastName') || '';
    return `${firstName} ${lastName}`.trim();
  }
}