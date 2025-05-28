import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket, TicketMessage } from '@models/ticket.model';
import { StorageService } from '@/components/_services/storage.service';

const TICKET_API = 'http://localhost:8081/api/tickets';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  constructor(
    private readonly http: HttpClient,
    private readonly storageService: StorageService
  ) {}

  createTicket(ticket: Ticket, message: TicketMessage): Observable<any> {
    const token = this.storageService.getUser()['token'];

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    const ticketCreateBody = {
      'subject': ticket['subject'],
      'type': ticket['type'],
      'firstMessage': message
    };

    return this.http.post(TICKET_API, ticketCreateBody, { headers });
  }

  getTickets(): Observable<Ticket[]> {
    const user = this.storageService.getUser();
    const token = user['token'];
    const roles: string[] = user['roles'] ?? [];

    const isAdminOrSecretary = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_SECRETARY');
    const getOnlyForUser = !isAdminOrSecretary;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams().set('getOnlyForUser', String(getOnlyForUser));

    return this.http.get<Ticket[]>(TICKET_API, { headers, params });
  }
}
