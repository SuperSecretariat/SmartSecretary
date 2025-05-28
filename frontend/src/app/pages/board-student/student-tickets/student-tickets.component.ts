import { TicketService } from '@/components/_services/ticket.service';
import { Component, OnInit } from '@angular/core';
import { Ticket, TicketMessage } from '@models/ticket.model';

@Component({
  selector: 'app-student-tickets',
  templateUrl: './student-tickets.component.html',
  styleUrls: ['./student-tickets.component.css'],
  standalone: false,
})
export class StudentTicketsComponent implements OnInit {
  tickets: Ticket[] = [];
  selectedTicket: Ticket | null = null;
  showChatModal = false;
  showCreateModal = false;

  searchQuery = '';
  filterStatus = '';
  filterType = '';

  currentUserName = 'Student Name';

  constructor(private readonly ticketService: TicketService) {}

  ngOnInit(): void {
    this.refreshUserTickets();
  }

  get filteredTickets(): Ticket[] {
    return this.tickets.filter(ticket =>
      ticket.subject.toLowerCase().includes(this.searchQuery.toLowerCase()) &&
      (!this.filterStatus || ticket.status === this.filterStatus) &&
      (!this.filterType || ticket.type === this.filterType)
    );
  }

  openChat(ticket: Ticket): void {
    this.selectedTicket = ticket;
    this.showChatModal = true;
  }

  closeChat(): void {
    this.selectedTicket = null;
    this.showChatModal = false;
  }

  handleSendMessage(payload: {ticket: Ticket, message: string, callback: (ticket: Ticket) => void}): void {
    this.ticketService.sendTicketMessage(payload.ticket, payload.message).subscribe({
      error: err => {
        console.error('Failed to send message:', err);
      },
      next: response => {
        console.log('Message sent:', response);
        payload.callback(payload.ticket)
      },
    })
  }

  refreshUserTickets(): void {
    this.ticketService.getTickets().subscribe({
      error: (err) => {
        console.error('Failed to load tickets:', err);
      },
      next: (tickets: Ticket[]) => {
        console.log('received tickets: ', tickets)
        this.tickets = tickets;
      },
    });
  }

  handleNewTicket(payload: { ticket: Ticket, message: TicketMessage }): void {
    this.ticketService.createTicket(payload['ticket'], payload['message']).subscribe({
      error: err => {
        console.error('Failed to create ticket:', err);
      },
      next: response => {
        console.log('Ticket created:', response);
        this.refreshUserTickets();
        this.showCreateModal = false;
      },
    });
  }
}
