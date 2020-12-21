import {Shape} from './Shape';

export interface RoomPerspective{
  yourShape: Shape | null;
  otherShapes: (Shape | null)[];
}
