import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError, tap, firstValueFrom } from 'rxjs';
import {response} from 'express';


export interface visit {
  patientID: number;
  admissionDateTime: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
}

export interface newvisit {
  patientID: number;
  reasonForVisit: String;
}

export interface servicePerVisit {
  patientID: number;
  visitAdmissionDateTime: string;
  performedBy:string;
  service: string;
}

export interface drugPerVisit {
  patientID: number;
  visitAdmissionDateTime: string;
  administeredBy: string;
  drug:number;
  quantity: number;
}

export interface roomForVisit{
  patientID: number;
  visitAdmissionDateTime: string;
  roomName: string;
}

export interface notesForVisit {
  patientID: number;
  visitAdmissionDateTime: string;
  note: String|undefined;
}

export interface dischargeForVisit {
  patientID: number;
  visitAdmissionDateTime: string;
  visitSummary :String;
}

export interface roomCancel{
  patientID: number;
  visitAdmissionDateTime: string;
}

export interface Doctor{
  username: string;
  firstName: string;
  lastName: string;
  specialty?: string; // Add if available
}

@Injectable({
  providedIn: 'root'
})
export class VisitMgmtService {
  private baseUrl = 'http://localhost:8080/visit';
  private recUrl = 'http://localhost:8080/record';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  createVisit(data: newvisit): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error creating visit:', error);
        return throwError(() => error);
      })
    );
  }

  addService(data: servicePerVisit): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/service`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error adding service:', error);
        return throwError(() => error);
      })
    );
  }

  addDrug(data: drugPerVisit): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/drug`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error adding drug:', error);
        return throwError(() => error);
      })
    );
  }

  assignRoom(data: roomForVisit): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/room`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error assigning room:', error);
        return throwError(() => error);
      })
    );
  }

  removeRoom(data: roomCancel): Observable<{ message: string }> {
    return this.http.request<{ message: string }>(
      'delete',
      `${this.baseUrl}/room`,
      {
        body: data,
        headers: this.getHeaders()
      }
    ).pipe(
      catchError(error => {
        console.error('Error removing room:', error);
        return throwError(() => error);
      })
    );
  }

  updateNote(data: notesForVisit): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(
      `${this.baseUrl}/note`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error updating note:', error);
        return throwError(() => error);
      })
    );
  }

  dischargeVisit(data: dischargeForVisit): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/discharge`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error discharging visit:', error);
        return throwError(() => error);
      })
    );
  }

  getVisits(): Observable<{ visits: visit[] }> {
    return this.http.get<{ visits: visit[] }>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching visits:', error);
        return throwError(() => error);
      })
    );
  }
  getRecords(): Observable<{ visits: visit[] }> {
    return this.http.get<{ visits: visit[] }>(
      `${this.recUrl}`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching visits:', error);
        return throwError(() => error);
      })
    );
  }

  getTimeline(patientID: number, admissionDateTime: string): Observable<{
    timeline: { dateTime: string; title: string; description: string }[];
    visitNote?: string; // Add this line to include the note in the response type
  }> {
    return this.http.get<{
      timeline: { dateTime: string; title: string; description: string }[];
      visitNote?: string;
    }>(
      `${this.baseUrl}/${patientID}/${admissionDateTime}`,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => console.log('Timeline response:', response)), // Add this tap operator to log the full response
      catchError(error => {
        console.error('Error fetching timeline:', error);
        return throwError(() => error);
      })
    );
  }

  getRecord(patientID: number, admissionDateTime: string): Observable<{
    timeline: { dateTime: string; title: string; description: string }[];
    visitNote?: string; // Add this line to include the note in the response type
  }> {
    return this.http.get<{
      timeline: { dateTime: string; title: string; description: string }[];
      visitNote?: string;
    }>(
      `${this.recUrl}/${patientID}/${admissionDateTime}`,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => console.log('Timeline response:', response)), // Add this tap operator to log the full response
      catchError(error => {
        console.error('Error fetching record timeline:', error);
        return throwError(() => error);
      })
    );
  }


  getDoctors(): Observable<{
    doctors: { username: string; firstName: string; lastName: string }[];
  }> {
    return this.http.get<{
      doctors: { username: string; firstName: string; lastName: string }[];
    }>(
      `${this.baseUrl}/doctors`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching doctors:', error);
        return throwError(() => error);
      })
    );
  }

  getServices(): Observable<{
    services: { name: string; category: string; description: string; cost: number }[];
  }> {
    return this.http.get<{
      services: { name: string; category: string; description: string; cost: number }[];
    }>(
      `${this.baseUrl}/services`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching services:', error);
        return throwError(() => error);
      })
    );
  }

  getDrugs(): Observable<{
    drugs: { ndc: number; name: string; strength: string }[];
  }> {
    return this.http.get<{
      drugs: { ndc: number; name: string; strength: string }[];
    }>(
      `${this.baseUrl}/drugs`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching drugs:', error);
        return throwError(() => error);
      })
    );
  }

  getRooms(): Observable<{
    rooms: { roomName: string; department: string }[];
  }> {
    return this.http.get<{
      rooms: { roomName: string; department: string }[];
    }>(
      `${this.baseUrl}/rooms`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error fetching rooms:', error);
        return throwError(() => error);
      })
    );
  }

  getPatientDetailsString(patientID: number): Promise<string> {
    return firstValueFrom(
      this.http.get<{
        firstName: string; lastName: string;
        dateOfBirth: string; gender: string;
        contactNumber: string; email: string;
        address: string; city: string;
        state: string; zipCode: string;
        insuranceProvider: string; insuranceNumber: string;
      }>(
        `http://localhost:8080/patient/${patientID}`,
        {
          headers: this.getHeaders(),
          observe: 'response' // To get full response including status
        }
      )
    ).then(response => {
      if (response.status === 404) {
        return 'Patient not found';
      }

      const data = response.body!;
      return `
      Name: ${data.firstName} ${data.lastName}
      DOB: ${data.dateOfBirth}
      Gender: ${data.gender}
      Contact: ${data.contactNumber}
      Email: ${data.email}
      Address: ${data.address}, ${data.city}, ${data.state}, ${data.zipCode}
      Insurance: ${data.insuranceProvider} (${data.insuranceNumber})
    `.trim();
    }).catch(err => {
      console.error('Failed to fetch patient details', err);
      if (err.status === 404) {
        return 'Patient not found';
      }
      return 'Unable to retrieve patient information.';
    });
  }

}
