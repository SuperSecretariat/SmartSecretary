import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8081/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(registrationNumber: string ,password: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'login',
      {
        registrationNumber,
        password,
      },
      httpOptions
    );
  }

  register(firstName: string, lastName: string, email: string, registrationNumber: string, dateOfBirth: string, cnp: string, university: string, faculty: string, password: string, confirmationPassword: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'register',
      {
        firstName,
        lastName,
        email,
        registrationNumber,
        dateOfBirth,
        cnp,
        university,
        faculty,
        password,
        confirmationPassword
      },
      {
        headers: httpOptions.headers,
        responseType: 'text'
      }
    );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_API + 'signout', { }, httpOptions);
  }
}