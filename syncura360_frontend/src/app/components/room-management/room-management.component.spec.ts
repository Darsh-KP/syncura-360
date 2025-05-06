import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RoomManagementComponent } from './room-management.component';
import { RoomService } from '../../services/room.service';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('RoomManagementComponent', () => {
  let component: RoomManagementComponent;
  let fixture: ComponentFixture<RoomManagementComponent>;
  let mockRoomService: jasmine.SpyObj<RoomService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    mockRoomService = jasmine.createSpyObj('RoomService', ['getAllRooms', 'createRoom', 'updateRoom', 'deleteRoom']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open', 'closeAll']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RoomManagementComponent],
      providers: [
        { provide: RoomService, useValue: mockRoomService },
        { provide: MatDialog, useValue: mockDialog }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomManagementComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load rooms on init', () => {
    const mockRooms = [{ roomName: '101A', department: 'ICU', beds: 2, equipments: [] }];
    mockRoomService.getAllRooms.and.returnValue(of({ rooms: mockRooms }));

    component.ngOnInit();

    expect(mockRoomService.getAllRooms).toHaveBeenCalled();
    expect(component.rooms).toEqual(mockRooms);
  });

  it('should open add room dialog and reset fields', () => {
    component.newRoomName = 'Test';
    component.newRoomDepartment = 'ICU';
    component.bedCount = 5;
    component.equipmentList = [{ name: 'Monitor', serialNo: 'ABC123', inMaintenance: false }];

    component.openAddRoomDialog();

    expect(component.newRoomName).toBe('');
    expect(component.newRoomDepartment).toBe('');
    expect(component.bedCount).toBe(0);
    expect(component.equipmentList.length).toBe(0);
    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should add equipment to the list', () => {
    component.equipmentNameInput = 'Ventilator';
    component.equipmentSerialInput = '123XYZ';

    component.addEquipment();

    expect(component.equipmentList.length).toBe(1);
    expect(component.equipmentList[0].name).toBe('Ventilator');
  });

  it('should save a room', () => {
    component.newRoomName = '101';
    component.newRoomDepartment = 'Emergency';
    component.bedCount = 3;
    component.equipmentList = [];

    mockRoomService.createRoom.and.returnValue(of({ message: 'Room created' }));
    spyOn(component, 'loadRooms');

    component.saveRoom();

    expect(mockRoomService.createRoom).toHaveBeenCalled();
    expect(component.loadRooms).toHaveBeenCalled();
    expect(mockDialog.closeAll).toHaveBeenCalled();
  });

  it('should return all rooms when search query is empty', () => {
    component.rooms = [{ roomName: '101', department: '', beds: 1, equipments: [] }];
    component.searchQuery = '';
    expect(component.filteredRooms.length).toBe(1);
  });

  it('should filter rooms based on search query', () => {
    component.rooms = [
      { roomName: '101A', department: '', beds: 1, equipments: [] },
      { roomName: '102B', department: '', beds: 2, equipments: [] }
    ];
    component.searchQuery = '102';

    expect(component.filteredRooms.length).toBe(1);
    expect(component.filteredRooms[0].roomName).toBe('102B');
  });

  it('should close all dialogs', () => {
    component.closeAllDialogs();
    expect(mockDialog.closeAll).toHaveBeenCalled();
  });
});