import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardAdminAddComponent } from './board-admin-add.component';

describe('BoardAdminAddComponent', () => {
  let component: BoardAdminAddComponent;
  let fixture: ComponentFixture<BoardAdminAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardAdminAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardAdminAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
