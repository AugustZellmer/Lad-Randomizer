import { Component, OnInit } from '@angular/core';
import {BackendService} from "../services/backend.service";
import {CurrentRoomService} from "../services/current-room.service";
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  constructor(private backend: BackendService, private currentRoom: CurrentRoomService, private router: Router) { }

  ngOnInit(): void {
  }

  createRoom(){
    this.backend.createRoom().subscribe(
      room => {
        console.log("Created room: "+room.roomId);
        this.currentRoom.set(room.roomId);
        this.router.navigate(['/room']);
      }
    )
  }

}
