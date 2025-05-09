import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8081/api/add/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json',
    'responseType': 'text'})
};

@Injectable({
  providedIn: 'root',
})
export class AddService {
  constructor(private readonly http: HttpClient) {}

  addAdmin(email: string ,authKey: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'admin',
      {
        email,
        authKey,
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }

  addSecretary(email: string ,authKey: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'secretary',
      {
        email,
        authKey,
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }

  addStudent(registrationNumber: string, email: string){
    return this.http.post(
      AUTH_API + 'student',
      {
        registrationNumber,
        email,
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    )
  }
}
