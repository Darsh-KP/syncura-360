import { Component, TemplateRef, ViewChild, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NavbarComponent } from '../navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { RoomService, Room, Equipment } from '../../services/room.service';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-room-management',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, MatIconModule, MatTooltipModule, MatInputModule, MatCheckboxModule],
  templateUrl: './room-management.component.html',
  styleUrls: ['./room-management.component.css']
})
export class RoomManagementComponent implements OnInit {

  rooms: Room[] = [];

  newRoomName: string = '';
  newRoomDepartment: string = '';
  bedCount: number = 0;
  equipmentNameInput: string = '';
  equipmentSerialInput: string = '';
  equipmentList: Equipment[] = [];
  equipmentInput: string = '';

  selectedRoom: Room | null = null;

  @ViewChild('addRoomDialog') addRoomDialog!: TemplateRef<any>;
  @ViewChild('roomDetailsDialog') roomDetailsDialog!: TemplateRef<any>;

  constructor(private dialog: MatDialog, private roomService: RoomService) {}

  ngOnInit(): void {
    this.loadRooms();
  }

  loadRooms() {
    this.roomService.getAllRooms().subscribe(response => {
      this.rooms = response.rooms || [];
    });
  }

  openAddRoomDialog() {
    this.newRoomName = '';
    this.newRoomDepartment = '';
    this.bedCount = 0;
    this.equipmentNameInput = '';
    this.equipmentSerialInput = '';
    this.equipmentList = [];
    this.dialog.open(this.addRoomDialog);
  }

  incrementBeds() { this.bedCount++; }
  decrementBeds() { if (this.bedCount > 0) this.bedCount--; }

  addEquipment() {
    if (this.equipmentNameInput.trim() && this.equipmentSerialInput.trim()) {
      this.equipmentList.push({
        name: this.equipmentNameInput.trim(),
        serialNo: this.equipmentSerialInput.trim(),
        inMaintenance: false
      });
      this.equipmentNameInput = '';
      this.equipmentSerialInput = '';
    }
  }

  saveRoom() {
    if (this.newRoomName.trim() === '' || this.newRoomDepartment.trim() === '') {
      return;
    }
    
    const newRoom: Room = {
      roomName: this.newRoomName,
      department: this.newRoomDepartment,
      beds: this.bedCount,
      equipments: [...this.equipmentList]
    };
    // console.log(newRoom);
    this.roomService.createRoom(newRoom).subscribe(() => {
      this.loadRooms();
      this.dialog.closeAll();
    });

    this.dialog.closeAll();
  }

  openRoomDetailsDialog(room: Room) {
    this.selectedRoom = JSON.parse(JSON.stringify(room));
    this.dialog.open(this.roomDetailsDialog);
  }

  incrementSelectedRoomBeds() {
    if (this.selectedRoom) {
      this.selectedRoom.beds++;
    }
}

decrementSelectedRoomBeds() {
    if (this.selectedRoom && this.selectedRoom.beds > 0) {
      this.selectedRoom.beds--;
    }
}


addEquipmentToSelectedRoom() {
  if (this.selectedRoom && this.equipmentNameInput.trim() && this.equipmentSerialInput.trim()) {
    this.selectedRoom.equipments.push({
      name: this.equipmentNameInput.trim(),
      serialNo: this.equipmentSerialInput.trim(),
      inMaintenance: false
    });
    this.equipmentNameInput = '';
    this.equipmentSerialInput = '';
  }
}


removeEquipmentFromSelectedRoom(index: number) {
  if (this.selectedRoom) {
    this.selectedRoom.equipments.splice(index, 1);
  }
}


  updateRoom(room: Room) {
    console.log("Updated Room Payload: ", room);
    this.roomService.updateRoom(room).subscribe(() => {
      this.loadRooms();
      this.dialog.closeAll(); 
    });
  }
  

  deleteSelectedRoom() {
    if (this.selectedRoom) {
      const confirmDelete = confirm(`Are you sure you want to delete the room "${this.selectedRoom.roomName}"?`);
      if (confirmDelete) {
        this.roomService.deleteRoom(this.selectedRoom.roomName).subscribe(() => {
          this.loadRooms();
          this.dialog.closeAll();
        });
      }
    }
  }  

  clearEquipmentInput() { this.equipmentInput = ''; }

  searchQuery: string = '';
  get filteredRooms(): Room[] {
    if (!this.searchQuery.trim()) {
      return this.rooms;
    }
    return this.rooms.filter(room => 
      room.roomName.toLowerCase().includes(this.searchQuery.trim().toLowerCase())
    );
  }

  public closeAllDialogs(): void {
    this.dialog.closeAll();
  }
}