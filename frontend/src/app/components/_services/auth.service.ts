import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from '../_services/storage.service';
import { environment } from '../../../environments/environments';

const AUTH_API = `${environment.backendUrl}/api/auth/`;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private readonly http: HttpClient, private readonly storageService: StorageService) {}

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

  register(firstName: string, lastName: string, email: string, registrationNumber: string, password: string, confirmationPassword: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'register',
      {
        firstName,
        lastName,
        email,
        registrationNumber,
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
    const token = this.storageService.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(AUTH_API + 'logout', { }, 
      {
        headers: headers,
        responseType: 'text'
      });
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(AUTH_API + 'forgot-password', {
      email
    },
     httpOptions);
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(AUTH_API + 'reset-password', {
      token,
      newPassword
    },
     httpOptions);
  }
}