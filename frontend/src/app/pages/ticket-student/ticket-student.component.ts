import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketComponent } from '../../components/ticket/ticket.component';

@Component({
  selector: 'app-ticket-student',
  templateUrl: './ticket-student.component.html',
  styleUrls: ['./ticket-student.component.css'],
  standalone: true,
  imports: [CommonModule, TicketComponent],
})
export class TicketStudentPageComponent {
  tickets = [
    { subject: 'Exam Delay', status: 'Open' },
    { subject: 'Transcript Request', status: 'Closed' }
  ];

  isModalOpen = false;

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }
}
