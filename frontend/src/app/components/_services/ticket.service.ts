import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket, TicketMessage } from '@models/ticket.model';
import { StorageService } from '@/components/_services/storage.service';
import { environment } from '@env/environments';

const TICKET_API = `${environment.backendUrl}/api/tickets`;

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
      'Authorization': `Bearer ${token}`,
    });

    const ticketCreateBody = {
      'subject': ticket['subject'],
      'type': ticket['type'],
      'firstMessage': message['message']
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
      'Authorization': `Bearer ${token}`,
    });

    const params = new HttpParams().set('getOnlyForUser', String(getOnlyForUser));

    return this.http.get<Ticket[]>(TICKET_API, { headers, params });
  }

  updateTicketStatus(updatedTicket: Ticket) {
    const token = this.storageService.getUser()['token']

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    })

    const params = new HttpParams().set('status', String(updatedTicket.status))

    const STATUS_API = `${TICKET_API}/${updatedTicket.id}/status`
    return this.http.patch<Ticket>(STATUS_API, {}, {headers, params})
  }

  getTicketMessages(ticket: Ticket) {
    const token = this.storageService.getUser()['token']

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    })

    const MESSAGES_API = `${TICKET_API}/${ticket.id}/messages`
    return this.http.get<TicketMessage[]>(MESSAGES_API, {headers})
  }

  sendTicketMessage(ticket: Ticket, message: string) {
    const token = this.storageService.getUser()['token']

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    })

    const params = new HttpParams().set('message', message)

    const MESSAGES_API = `${TICKET_API}/${ticket.id}/messages`
    return this.http.post(MESSAGES_API, {}, {headers, params})
  }
}
