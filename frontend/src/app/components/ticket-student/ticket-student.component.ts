import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StorageService } from '../_services/storage.service';
import { TicketService } from '../_services/ticket.service';

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
  submittedTickets: { id: number; ticketType: string; subject: string; status: string }[] = [];
  currentUserId: string = '';
  ticketSubject: string = '';
  ticketMessage: string = '';
  errorMessage: string = '';
  tickets: { id: number; ticketType: string; subject: string; message: string; status: string }[] = [];
  selectedTickets: { id: number; ticketType: string; subject: string, message: string; status: string }[] = [];
  constructor(private router: Router,
    private storageService: StorageService,
    private ticketService: TicketService) {
    this.currentUserId = storageService.getRegistrationNumber();

    const storedTickets = localStorage.getItem(`tickets_${this.currentUserId}`);
    if (storedTickets) {
      this.tickets = JSON.parse(storedTickets);
    }

    const storedSubmittedTickets = localStorage.getItem(`submitted_tickets_${this.currentUserId}`);
    if (storedSubmittedTickets) {
      this.submittedTickets = JSON.parse(storedSubmittedTickets);
    }
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
        subject: this.ticketSubject.trim(),
        message: this.ticketMessage.trim(),
        status: 'Pending'
      };

      this.tickets.push(newTicket);
      localStorage.setItem(`tickets_${this.currentUserId}`, JSON.stringify(this.tickets));

      alert(`Your ticket for "${this.selectedTicket}" has been created.`);
      this.selectedTicket = null;
      this.ticketMessage = '';
    }
  }


  goToHome(): void {
    this.router.navigate(['/submitted-tickets']); // Adjust route if needed
  }

  toggleTicketSelection(ticket: { id: number; ticketType: string; subject: string; message: string; status: string }): void {
    const index = this.selectedTickets.findIndex(t => t.id === ticket.id);
    if (index === -1) {
      this.selectedTickets.push(ticket);
    } else {
      this.selectedTickets.splice(index, 1);
    }
  }

  submitTickets(): void {
    this.selectedTickets.forEach(ticket => {
      ticket.status = 'SUBMITTED';
      this.submittedTickets.push(ticket);
      this.ticketService.sendTicket(
        ticket.subject,
        ticket.message,
        ticket.ticketType,
        ticket.status,
        this.currentUserId
      ).subscribe({
          next: data => {
            console.log(data);
          },
          error: err => {
            console.log(err);
          }
        });
    });

    localStorage.setItem(`submitted_tickets_${this.currentUserId}`, JSON.stringify(this.submittedTickets));
    this.tickets = this.tickets.filter(ticket => !this.selectedTickets.includes(ticket));
    this.selectedTickets = [];
    localStorage.setItem(`tickets_${this.currentUserId}`, JSON.stringify(this.tickets));
    alert('The selected tickets have been submitted!');
  }
}