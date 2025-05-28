import { Component } from '@angular/core';
import { Ticket, TicketMessage, TicketStatus } from '@models/ticket.model';

@Component({
  selector: 'app-secretary-tickets',
  templateUrl: './secretary-tickets.component.html',
  styleUrls: ['./secretary-tickets.component.css'],
  standalone: false,
})
export class SecretaryTicketsComponent {
  tickets: Ticket[] = []; // Populate from service
  selectedTicket: Ticket | null = null;
  showChatModal = false;

  searchQuery = '';
  filterStatus = '';
  filterType = '';

  currentUserName = 'Secretary Name'; // Should be dynamic

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

  handleSendMessage(message: TicketMessage): void {
    // this.selectedTicket?.addMessage(message);
    // update backend here
  }

  handleStatusChange(newStatus: String): void {
  }
}
