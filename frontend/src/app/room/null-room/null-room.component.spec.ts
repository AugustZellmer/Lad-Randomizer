import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NullRoomComponent } from './null-room.component';

describe('NullRoomComponent', () => {
  let component: NullRoomComponent;
  let fixture: ComponentFixture<NullRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NullRoomComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NullRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
