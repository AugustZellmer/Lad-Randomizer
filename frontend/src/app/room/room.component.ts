import { Component, OnInit } from '@angular/core';
import {BackendService} from '../services/backend.service';
import {CurrentRoomService} from '../services/current-room.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.css']
})
export class RoomComponent implements OnInit {

  private room: string = null;
  nullRoomErr = false;

  constructor(private backend: BackendService, private currentRoom: CurrentRoomService, private router: Router) { }

  ngOnInit(): void {
    this.room = this.currentRoom.get();
    if (this.room === null){
      this.nullRoomErr = true;
    }
  }

}
