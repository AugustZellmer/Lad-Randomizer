import { Injectable } from '@angular/core';
import {Room} from './businessObjects/Room';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {User} from './businessObjects/User';
import {RoomPerspective} from "./businessObjects/RoomPerspective";

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

  heartbeat(roomId: string, userId: string, lastUpdatedAt: Date): Observable<RoomPerspective>{
    // the next line is not best practice, but best practice doesn't work for me.
    return this.http.post<RoomPerspective>(
      `/api/room/${roomId}/user/${userId}/heartbeat?lastUpdatedAt=${lastUpdatedAt.toISOString()}`,
      null);
  }
}
