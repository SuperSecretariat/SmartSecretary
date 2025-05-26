import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketComponent } from '../../components/ticket/ticket.component';

@Component({
  selector: 'app-ticket-secretary',
  standalone: true,
  imports: [CommonModule, TicketComponent],
  templateUrl: './ticket-secretary.component.html',
  styleUrls: ['./ticket-secretary.component.css']
})
export class TicketSecretaryPageComponent {
  tickets = [
    { subject: 'Exam Delay', status: 'Open' },
    { subject: 'Schedule Change Request', status: 'Pending' },
    { subject: 'Transcript Request', status: 'Closed' }
  ];
}
