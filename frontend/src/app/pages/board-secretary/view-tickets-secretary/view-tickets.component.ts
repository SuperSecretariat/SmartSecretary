import { Component, OnInit } from '@angular/core';
import { TicketService } from '../../../components/_services/ticket.service';

@Component({
  selector: 'app-view-tickets',
  templateUrl: './view-tickets.component.html',
  styleUrls: ['./view-tickets.component.css'],
  standalone: false
})
export class ViewTicketsSecretaryComponent implements OnInit {
  tickets: { id: number; subject: string; message: string; ticketType: string; status: string; timestamp: string; registrationNumber: string }[] = [];
  expandedTicketId: number | null = null; // To handle expanded/collapsed state

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.getTickets();
  }

  getTickets(): void {
    this.ticketService.getTickets().subscribe({
      next: (response: any) => {
        this.tickets = response;
      },
      error: (error) => {
        console.error('Error fetching tickets:', error);
      }
    });
  }

  toggleTicket(ticketId: number): void {
    this.expandedTicketId = this.expandedTicketId === ticketId ? null : ticketId;
  }

  finishTicket(ticketId: number):void {
    this.ticketService.finishTicket(ticketId).subscribe({
      next: (response: any) => {
        console.log(response);
      },
      error: (error) => {
        console.error('Error fetching tickets:', error);
      }
    });
  }

  respondToTicket(ticketId: number): void {
    console.log(`Responding to ticket with ID: ${ticketId}`); // Replace with your respond ticket logic
  }

  closeTicket(ticketId: number): void {
    this.ticketService.closeTicket(ticketId).subscribe({
      next: (response: any) => {
        console.log(response);
      },
      error: (error) => {
        console.error('Error fetching tickets:', error);
      }
    });
  }
}
