import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CurrentRoomService {

  constructor() { }

  set(roomId: string){
    window.localStorage.setItem("roomId", roomId);
  }

  get(): string | null{
    return window.localStorage.getItem("roomId");
  }
}
