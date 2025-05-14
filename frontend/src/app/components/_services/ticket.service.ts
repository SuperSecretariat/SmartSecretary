import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const TICKET_API = 'http://localhost:8081/api/tickets';

const httpOptions = {
  headers: new HttpHeaders({ 
    'Content-Type': 'application/json',
    'responseType': 'text'
  })
};

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  constructor(private readonly http: HttpClient) {};

  sendTicket(subject: string, message: string, type: string, status: string, registrationNumber: string): Observable<any> {
    return this.http.post(
      TICKET_API,
      {
        subject,
        message,
        type,
        status,
        registrationNumber
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }

  getTickets(){
    return this.http.get<string>(
      TICKET_API,
      {
        headers: httpOptions.headers,
      }
    );
  }

  finishTicket(ticketId: number){
    return this.http.post(
      TICKET_API + "/finish",
      {
        "ticketId" : ticketId
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }

  closeTicket(ticketId: number){
    return this.http.post(
      TICKET_API + "/close",
      {
        "ticketId" : ticketId
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }
}