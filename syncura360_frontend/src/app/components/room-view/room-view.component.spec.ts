import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RoomViewComponent } from './room-view.component';
import { MatDialog } from '@angular/material/dialog';
import { RoomService, Room } from '../../services/room.service';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('RoomViewComponent', () => {
  let component: RoomViewComponent;
  let fixture: ComponentFixture<RoomViewComponent>;
  let mockRoomService: jasmine.SpyObj<RoomService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  const mockRooms: Room[] = [
    {
      roomName: 'ICU-1',
      department: 'ICU',
      beds: 4,
      bedsVacant: 1,
      bedsOccupied: 2,
      bedsMaintenance: 1,
      equipments: []
    },
    {
      roomName: 'ER-2',
      department: 'Emergency',
      beds: 3,
      bedsVacant: 2,
      bedsOccupied: 1,
      bedsMaintenance: 0,
      equipments: []
    }
  ];

  beforeEach(async () => {
    mockRoomService = jasmine.createSpyObj('RoomService', ['getAllRooms']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open', 'closeAll']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RoomViewComponent],
      providers: [
        { provide: RoomService, useValue: mockRoomService },
        { provide: MatDialog, useValue: mockDialog }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomViewComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load rooms on init', () => {
    mockRoomService.getAllRooms.and.returnValue(of({ rooms: mockRooms }));
    component.ngOnInit();
    expect(mockRoomService.getAllRooms).toHaveBeenCalled();
    expect(component.rooms.length).toBe(2);
  });

  it('should return all rooms when search query is empty', () => {
    component.rooms = mockRooms;
    component.searchQuery = '';
    expect(component.filteredRooms.length).toBe(2);
  });

  it('should filter rooms based on search query', () => {
    component.rooms = mockRooms;
    component.searchQuery = 'ICU';
    const result = component.filteredRooms;
    expect(result.length).toBe(1);
    expect(result[0].roomName).toContain('ICU');
  });

  it('should open room details dialog and clone selected room', () => {
    const mockRoom = mockRooms[0];
    component.roomDetailsDialog = {} as any; // mock the ViewChild
    component.openRoomDetailsDialog(mockRoom);
    expect(component.selectedRoom).toEqual(mockRoom);
    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should close all dialogs', () => {
    component.closeAllDialogs();
    expect(mockDialog.closeAll).toHaveBeenCalled();
  });
});