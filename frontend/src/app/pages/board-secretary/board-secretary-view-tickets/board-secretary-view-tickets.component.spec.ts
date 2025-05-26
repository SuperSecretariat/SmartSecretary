import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSecretaryViewTicketsComponent } from './board-secretary-view-tickets.component';

describe('BoardSecretaryViewTicketsComponent', () => {
  let component: BoardSecretaryViewTicketsComponent;
  let fixture: ComponentFixture<BoardSecretaryViewTicketsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardSecretaryViewTicketsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardSecretaryViewTicketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
