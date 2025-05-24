import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardAdminShowAuthKeyComponent } from './board-admin-show-auth-key.component';

describe('BoardAdminShowAuthKeyComponent', () => {
  let component: BoardAdminShowAuthKeyComponent;
  let fixture: ComponentFixture<BoardAdminShowAuthKeyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardAdminShowAuthKeyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardAdminShowAuthKeyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
