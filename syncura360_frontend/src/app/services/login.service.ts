import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

interface LoginResponse {
  message: string;
  role: string;
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
        if (authHeader && authHeader.startsWith('Bearer ')) {
          const token = authHeader.split(' ')[1]; 
          localStorage.setItem('token', token);
        } else {
          console.warn('Authorization header missing or improperly formatted');
        }
  
        const responseBody: LoginResponse | null = response.body;
        if (responseBody) {
          localStorage.setItem('role', responseBody.role);
        }
      }),
      catchError(error => {
        console.error('Login error:', error);
        return throwError(() => new Error(error.error || 'Login failed. Please try again.'));
      })
    );    
  }
  
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    return token !== null && token !== '';
  }  

  getToken(): string | null {
    const token = localStorage.getItem('token');
    return token ? `Bearer ${token}` : null;
  }
}