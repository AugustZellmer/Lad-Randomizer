import { Injectable } from '@angular/core';
import {Room} from './businessObjects/Room';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {User} from './businessObjects/User';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http: HttpClient) { }

  createRoom(): Observable<Room>{
    return this.http.post<Room>('/api/room', null);
  }

  createUser(roomId: string): Observable<User>{
    return this.http.post<User>(`/api/room/${roomId}/user`, null);
  }
}
