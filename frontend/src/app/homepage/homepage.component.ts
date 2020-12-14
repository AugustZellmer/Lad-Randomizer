import { Component, OnInit } from '@angular/core';
import {BackendService} from '../services/backend.service';
import {CurrentRoomService} from '../services/current-room.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  roomInput: string;
  inputError: boolean = false;

  constructor(private backend: BackendService, private currentRoom: CurrentRoomService, private router: Router) { }

  ngOnInit(): void {
  }

  createRoom(): void{
    this.backend.createRoom().subscribe(
      room => {
        console.log('created room [' + room.roomId + ']');
        this.currentRoom.set(room.roomId);
        this.router.navigate(['/room']);
      }
    );
  }

  joinRoom(): void{
    if (this.roomInput === undefined || this.roomInput === null || this.roomInput === ''){
      this.inputError = true;
      setTimeout(() => this.inputError = false, 500); // the time it takes for the animation to play
      return;
    }
    console.log('joining room [' + this.roomInput + ']');
    this.currentRoom.set(this.roomInput);
    this.router.navigate(['/room']);
  }
}
