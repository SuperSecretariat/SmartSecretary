import { TicketService } from '@/components/_services/ticket.service';
import { Component, OnInit } from '@angular/core';
import { Ticket, TicketMessage, TicketStatus } from '@models/ticket.model';

@Component({
  selector: 'app-secretary-tickets',
  templateUrl: './secretary-tickets.component.html',
  styleUrls: ['./secretary-tickets.component.css'],
  standalone: false,
})
export class SecretaryTicketsComponent implements OnInit {
  tickets: Ticket[] = []; // Populate from service
  selectedTicket: Ticket | null = null;
  showChatModal = false;

  searchQuery = '';
  filterStatus = '';
  filterType = '';

  currentUserName = 'Secretary Name'; // Should be dynamic
  
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

  openChat(ticket: Ticket): void {
    this.selectedTicket = ticket;
    this.showChatModal = true;
  }

  closeChat(): void {
    this.selectedTicket = null;
    this.showChatModal = false;
  }

  handleSendMessage(message: TicketMessage): void {
    // this.selectedTicket?.addMessage(message);
    // update backend here
  }

  handleStatusChange(updatedTicket: Ticket): void {
    this.ticketService.updateTicketStatus(updatedTicket).subscribe({
      error: err => {
        console.error('Failed to update ticket:', err);
      },
      next: response => {
        console.log('Ticket updated:', response);
        this.refreshUserTickets();
        this.showChatModal = false;
      },
    })
  }
}
