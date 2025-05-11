import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-ticket-student',
  templateUrl: './ticket-student.component.html',
  styleUrls: ['./ticket-student.component.css'],
  imports: [CommonModule, FormsModule]
})
export class TicketStudentComponent {
  ticketTypes = ['Bug Report', 'Support Request', 'Feature Request', 'Other'];
  selectedTicket: string | null = null;
  submittedTickets: { id: number; ticketType: string; status: string }[] = [];
  currentUserId: string = '';
  ticketMessage: string = '';
  tickets: { id: number; ticketType: string; message: string; status: string }[] = [];
  selectedTickets: { id: number; ticketType: string; message: string; status: string }[] = [];
  constructor(private router: Router) {
    this.currentUserId = this.getLoggedInUserId();

    const storedTickets = localStorage.getItem(`tickets_${this.currentUserId}`);
    if (storedTickets) {
      this.tickets = JSON.parse(storedTickets);
    }

    const storedSubmittedTickets = localStorage.getItem(`submitted_tickets_${this.currentUserId}`);
    if (storedSubmittedTickets) {
      this.submittedTickets = JSON.parse(storedSubmittedTickets);
    }
  }

  getLoggedInUserId(): string {
    return 'user123';
  }

  selectTicket(ticket: string): void {
    this.selectedTicket = ticket;
  }

  addTicket(): void {
    if (this.selectedTicket && this.ticketMessage.trim()) {
      const allTickets = [...this.tickets];
      const maxId = allTickets.length > 0 ? Math.max(...allTickets.map(t => t.id)) : 0;

      const newTicket = {
        id: maxId + 1,
        ticketType: this.selectedTicket,
        message: this.ticketMessage.trim(),
        status: 'Pending'
      };

      this.tickets.push(newTicket);
      localStorage.setItem(`tickets_${this.currentUserId}`, JSON.stringify(this.tickets));

      alert(`Your ticket for "${this.selectedTicket}" has been created with ID ${newTicket.id}.`);
      this.selectedTicket = null;
      this.ticketMessage = '';
    }
  }


  goToHome(): void {
    this.router.navigate(['/submitted-tickets']); // Adjust route if needed
  }

  toggleTicketSelection(ticket: { id: number; ticketType: string; message : string ; status: string }): void {
    const index = this.selectedTickets.findIndex(t => t.id === ticket.id);
    if (index === -1) {
      this.selectedTickets.push(ticket);
    } else {
      this.selectedTickets.splice(index, 1);
    }
  }

  submitTickets(): void {
    this.selectedTickets.forEach(ticket => {
      ticket.status = 'Submitted';
      this.submittedTickets.push(ticket);
    });

    localStorage.setItem(`submitted_tickets_${this.currentUserId}`, JSON.stringify(this.submittedTickets));
    this.tickets = this.tickets.filter(ticket => !this.selectedTickets.includes(ticket));
    this.selectedTickets = [];
    localStorage.setItem(`tickets_${this.currentUserId}`, JSON.stringify(this.tickets));
    alert('The selected tickets have been submitted!');
  }
}
