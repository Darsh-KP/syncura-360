import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Equipment {
  serialNo: string;
  name: string;
  status: 'available' | 'maintenance';
}

export interface Room {
  roomName: string;
  department: string;
  beds: number;
  equipment: Equipment[];
}

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private baseUrl = 'http://localhost:8080/room';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

  /**
   * Get all rooms
   */
  getAllRooms(): Observable<{ drugs: Room[] }> {
    return this.http.get<{ drugs: Room[] }>(`${this.baseUrl}`);
  }

  /**
   * Get a specific room by roomName
   */
  getRoomByName(roomName: string): Observable<Room> {
    return this.http.get<Room>(`${this.baseUrl}?roomName=${encodeURIComponent(roomName)}`);
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
    return this.http.request<{ message: string }>('delete', `${this.baseUrl}`, {
      headers: this.getHeaders(),
      body: { roomName }
    });
  }
}