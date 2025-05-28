import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketChatModalComponent } from './ticket-chat-modal.component';

describe('TicketChatModalComponent', () => {
  let component: TicketChatModalComponent;
  let fixture: ComponentFixture<TicketChatModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TicketChatModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketChatModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
