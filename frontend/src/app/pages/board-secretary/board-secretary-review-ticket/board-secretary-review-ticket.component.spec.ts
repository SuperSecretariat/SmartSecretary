import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSecretaryReviewTicketComponent } from './board-secretary-review-ticket.component';

describe('BoardSecretaryReviewTicketComponent', () => {
  let component: BoardSecretaryReviewTicketComponent;
  let fixture: ComponentFixture<BoardSecretaryReviewTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardSecretaryReviewTicketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardSecretaryReviewTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
