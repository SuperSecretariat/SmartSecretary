import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from '../_services/storage.service';
import { environment } from '../../../environments/environments';

const AUTH_API = `${environment.backendUrl}/api/add/`;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json',
    'responseType': 'text'})
};

@Injectable({
  providedIn: 'root',
})
export class AddService {
  constructor(private readonly http: HttpClient, private readonly storageService: StorageService) {}

  addAdmin(email: string ,authKey: string): Observable<any> {
    const token = this.storageService.getUser().token;
    console.log(token);
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(
      AUTH_API + 'admin',
      {
        email,
        authKey,
      },
      {
        headers: headers,
        responseType: 'text'
      }
    );
  }

  addSecretary(email: string ,authKey: string): Observable<any> {
    const token = this.storageService.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(
      AUTH_API + 'secretary',
      {
        email,
        authKey,
      },
      {
        headers: headers,
        responseType: 'text'
      }
    );
  }

  addStudent(registrationNumber: string, email: string){
    const token = this.storageService.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(
      AUTH_API + 'student',
      {
        registrationNumber,
        email,
      },
      {
        headers: headers,
        responseType: 'text'
      }
    )
  }
}
