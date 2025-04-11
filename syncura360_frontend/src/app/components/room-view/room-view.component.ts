import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NavbarComponent } from '../navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { RoomService, Room } from '../../services/room.service';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-room-view',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, MatIconModule, MatTooltipModule, MatInputModule],
  templateUrl: './room-view.component.html',
  styleUrls: ['./room-view.component.css']
})
export class RoomViewComponent implements OnInit {
  rooms: Room[] = [];
  selectedRoom: Room | null = null;
  searchQuery: string = '';

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
  
  openRoomDetailsDialog(room: Room) {
    this.selectedRoom = JSON.parse(JSON.stringify(room));
    this.dialog.open(this.roomDetailsDialog);
  }

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
