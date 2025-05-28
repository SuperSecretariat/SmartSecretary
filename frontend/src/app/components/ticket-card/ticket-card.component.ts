import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Ticket } from '@models/ticket.model';

@Component({
  selector: 'app-ticket-card',
  templateUrl: './ticket-card.component.html',
  styleUrls: ['./ticket-card.component.css'],
  standalone: false
})
export class TicketCardComponent {
  @Input() ticket!: Ticket;
  @Output() openChat = new EventEmitter<void>();

  onCardClick(): void {
    this.openChat.emit();
  }
}