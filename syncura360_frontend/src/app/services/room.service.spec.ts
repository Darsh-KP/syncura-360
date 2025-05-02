import { TestBed } from '@angular/core/testing';
import { RoomService, Room } from './room.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpHeaders } from '@angular/common/http';

describe('RoomService', () => {
  let service: RoomService;
  let httpMock: HttpTestingController;

  const mockRoom: Room = {
    roomName: 'Room A',
    department: 'Cardiology',
    beds: 3,
    bedsVacant: 1,
    bedsOccupied: 2,
    bedsMaintenance: 0,
    equipments: [
      { serialNo: 'EQ123', name: 'ECG Machine', inMaintenance: false }
    ]
  };

  beforeEach(() => {
    localStorage.setItem('token', 'test-token');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RoomService]
    });

    service = TestBed.inject(RoomService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all rooms', () => {
    const mockResponse = { rooms: [mockRoom] };

    service.getAllRooms().subscribe(response => {
      expect(response.rooms.length).toBe(1);
      expect(response.rooms[0].roomName).toBe('Room A');
    });

    const req = httpMock.expectOne('http://localhost:8080/room');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockResponse);
  });

  it('should get a room by name', () => {
    service.getRoomByName('Room A').subscribe(response => {
      expect(response.roomName).toBe('Room A');
    });

    const req = httpMock.expectOne('http://localhost:8080/room');
    expect(req.request.method).toBe('GET');
    expect(req.request.body).toEqual({ roomName: 'Room A' });
    req.flush(mockRoom);
  });

  it('should create a new room', () => {
    const mockResponse = { message: 'Room created successfully' };

    service.createRoom(mockRoom).subscribe(response => {
      expect(response.message).toBe('Room created successfully');
    });

    const req = httpMock.expectOne('http://localhost:8080/room');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRoom);
    req.flush(mockResponse);
  });

  it('should update a room', () => {
    const mockResponse = { message: 'Room updated successfully' };

    service.updateRoom(mockRoom).subscribe(response => {
      expect(response.message).toBe('Room updated successfully');
    });

    const req = httpMock.expectOne('http://localhost:8080/room');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockRoom);
    req.flush(mockResponse);
  });

  it('should delete a room by name', () => {
    const mockResponse = { message: 'Room deleted successfully' };

    service.deleteRoom('Room A').subscribe(response => {
      expect(response.message).toBe('Room deleted successfully');
    });

    const req = httpMock.expectOne('http://localhost:8080/room');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.body).toEqual({ roomName: 'Room A' });
    req.flush(mockResponse);
  });
});
