import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getAllPatients(): Observable<any[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.get<any[]>(`${this.apiUrl}/patients`, { headers });
  }

  getPatientById(id: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.get<any>(`${this.apiUrl}/patient/${id}`, { headers });
  }

  addNoteToPatient(id: number, note: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.post<any>(`${this.apiUrl}/patient/${id}/note`, { note }, { headers });
  }
}
