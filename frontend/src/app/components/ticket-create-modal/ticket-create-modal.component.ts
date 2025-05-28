import { Component, Output, EventEmitter } from '@angular/core';
import { Ticket, TicketMessage, TicketType } from '@models/ticket.model';

@Component({
  selector: 'app-ticket-create-modal',
  templateUrl: './ticket-create-modal.component.html',
  styleUrls: ['./ticket-create-modal.component.css'],
  standalone: false,
})
export class TicketCreateModalComponent {
  @Output() submitTicket = new EventEmitter<{ ticket: Ticket, message: TicketMessage }>();
  @Output() closeModal = new EventEmitter<void>();

  subject: String = '';
  type: TicketType = TicketType.BUG_REPORT;
  message: String = '';

  ticketTypes = [
    { label: 'Bug Report', value: TicketType.BUG_REPORT },
    { label: 'Information Request', value: TicketType.INFORMATION_REQUEST },
    { label: 'Feature Request', value: TicketType.FEATURE_REQUEST },
  ];

  onSubmit(): void {
    if (!this.subject.trim() || !this.type || !this.message.trim()) {
      return;
    }

    const initialMessage = new TicketMessage({
      message: this.message.trim(),
      timestamp: new Date(),
    });

    const newTicket = new Ticket({
      subject: this.subject.trim(),
      type: this.type,
    });

    const payload = {
      ticket: newTicket,
      message: initialMessage
    }

    this.submitTicket.emit(payload);
    this.resetForm();
  }

  onCancel(): void {
    this.resetForm();
    this.closeModal.emit();
  }

  private resetForm(): void {
    this.subject = '';
    this.type = TicketType.BUG_REPORT;
    this.message = '';
  }
}
