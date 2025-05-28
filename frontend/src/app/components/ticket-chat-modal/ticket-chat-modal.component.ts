import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Ticket, TicketMessage, TicketStatus, TicketStatusLabels } from '@models/ticket.model';

@Component({
  selector: 'app-ticket-chat-modal',
  templateUrl: './ticket-chat-modal.component.html',
  styleUrls: ['./ticket-chat-modal.component.css'],
  standalone: false,
})
export class TicketChatModalComponent {
  TicketStatus = TicketStatus;
  TicketStatusLabels = TicketStatusLabels;

  @Input() ticket!: Ticket;
  @Input() userRole!: 'student' | 'secretary';
  @Input() currentUserName!: string;

  @Output() closeModal = new EventEmitter<void>();
  @Output() sendMessage = new EventEmitter<TicketMessage>();
  @Output() changeStatus = new EventEmitter<string>();

  messageText = '';

  onSend(): void {
    const content = this.messageText.trim();
    if (!content) return;

    const message = new TicketMessage({
      sender: this.currentUserName,
      content,
      timestamp: new Date(),
    });

    this.sendMessage.emit(message);
    // this.ticket.addMessage(message);
    this.messageText = '';
  }

  onStatusChange(): void {
    const newStatus = this.ticket.status === TicketStatus.OPEN ? TicketStatus.CLOSED : TicketStatus.OPEN;
    this.ticket.status = newStatus;
    this.changeStatus.emit(newStatus);
  }

  onClose(): void {
    this.closeModal.emit();
  }
}
