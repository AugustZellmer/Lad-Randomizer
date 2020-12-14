import {Shape} from './Shape';

export interface User{
  roomId: string;
  userId: string;
  shape: Shape | null;
  lastSeenAt: Date;
}
