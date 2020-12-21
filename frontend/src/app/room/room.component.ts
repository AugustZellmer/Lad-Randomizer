import { Component, OnInit } from '@angular/core';
import {BackendService} from '../services/backend.service';
import {CurrentRoomService} from '../services/current-room.service';
import {Router} from '@angular/router';
import {RoomPerspective} from "../services/businessObjects/RoomPerspective";

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.css']
})
export class RoomComponent implements OnInit {

  private roomId: string = null;
  private userId: string = null;
  private lastCheckedAt: Date = new Date(1970, 1, 1);
  nullRoomErr = false;

  constructor(private backend: BackendService, private currentRoom: CurrentRoomService) { }

  ngOnInit(): void {
    this.roomId = this.currentRoom.get();
    if (this.roomId === null){
      this.nullRoomErr = true;
    }
    this.backend.createUser(this.roomId).subscribe(
      user => {
        console.log(`created user [${user.userId}]`);
        this.userId = user.userId;
        this.heartbeat();
      }
    );
  }

  heartbeat(): void{
    this.backend.heartbeat(this.roomId, this.userId, this.lastCheckedAt).subscribe(
      roomPerspective => {
        console.log(`heartbeat [${JSON.stringify(roomPerspective)}]`);
      }
    );
  }
}
