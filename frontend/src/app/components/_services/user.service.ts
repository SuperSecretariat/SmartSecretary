import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8081/api/test/'; //De modificat dupa ce face Vilcu AUTH_API-ul in backend

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getPublicContent(): Observable<any> {
    return this.http.get(API_URL + 'all', { responseType: 'text' });
  }

  getSecretaryBoard(): Observable<any> {
    return this.http.get(API_URL + 'secretary', { responseType: 'text' });
  }
  
  getStudentBoard(): Observable<any> {
    return this.http.get(API_URL + 'student', { responseType: 'text' });
  }

  getAdminBoard(): Observable<any> {
    return this.http.get(API_URL + 'admin', { responseType: 'text' });
  }
}