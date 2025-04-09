import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Equipment {
  serialNo: string;
  name: string;
  inMaintenance: boolean;
}

export interface Room {
  roomName: string;
  department: string;
  beds: number;
  bedsVacant?: number;
  bedsOccupied?: number;
  bedsMaintenance?: number;
  equipments: Equipment[];
}

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private baseUrl = 'http://localhost:8080/room';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  /**
   * Get all rooms
   */
  getAllRooms(): Observable<{ rooms: Room[] }> {
    return this.http.get<{ rooms: Room[] }>(`${this.baseUrl}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Get a specific room by roomName
   */
  getRoomByName(roomName: string): Observable<Room> {
    return this.http.request<Room>('GET', `${this.baseUrl}`, {
      headers: this.getHeaders(),
      body: { roomName }
    });
  }

  /**
   * Create a new room
   */
  createRoom(room: Room): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}`, room, {
      headers: this.getHeaders()
    });
  }

  /**
   * Update an existing room
   */
  updateRoom(room: Room): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.baseUrl}`, room, {
      headers: this.getHeaders()
    });
  }

  /**
   * Delete a room by roomName
   */
  deleteRoom(roomName: string): Observable<{ message: string }> {
    return this.http.request<{ message: string }>('DELETE', `${this.baseUrl}`, {
      headers: this.getHeaders(),
      body: { roomName }
    });
  }
}