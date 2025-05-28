import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Ticket, TicketMessage, TicketStatus, TicketStatusLabels } from '@models/ticket.model';
import { TicketService } from '../_services/ticket.service';

@Component({
  selector: 'app-ticket-chat-modal',
  templateUrl: './ticket-chat-modal.component.html',
  styleUrls: ['./ticket-chat-modal.component.css'],
  standalone: false,
})
export class TicketChatModalComponent implements OnInit {
  TicketStatus = TicketStatus;
  TicketStatusLabels = TicketStatusLabels;

  @Input() ticket!: Ticket;
  @Input() userRole!: 'student' | 'secretary';
  @Input() currentUserName!: string;

  @Output() closeModal = new EventEmitter<void>();
  @Output() sendMessage = new EventEmitter<{ticket: Ticket, message: string, callback: (ticket: Ticket) => void}>();
  @Output() changeStatus = new EventEmitter<Ticket>();

  messageText = '';
  ticketMessages: TicketMessage[] = [];

  constructor(private readonly ticketService: TicketService) {}

  ngOnInit(): void {
    this.getMessages(this.ticket)
  }

  onSend(): void {
    const content = this.messageText.trim();
    if (!content) return;
    
    const payload = {
      ticket: this.ticket,
      message: content,
      callback: this.getMessages.bind(this)
    }

    this.sendMessage.emit(payload);
    this.messageText = '';
  }

  getMessages(ticket: Ticket): void {
    this.ticketService.getTicketMessages(ticket).subscribe({
      error: (err) => {
        console.error('Failed to load messages:', err);
      },
      next: (messages: TicketMessage[]) => {
        console.log('received messages: ', messages)
        this.ticketMessages = messages;
      },
    })
  }

  onStatusChange(): void {
    const newStatus = ( this.ticket.status === TicketStatus.OPEN ? TicketStatus.CLOSED : TicketStatus.OPEN );
    this.ticket.status = newStatus;
    this.changeStatus.emit(this.ticket);
  }

  onClose(): void {
    this.closeModal.emit();
  }
}
