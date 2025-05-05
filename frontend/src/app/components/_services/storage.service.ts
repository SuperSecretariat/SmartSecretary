import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const USER_KEY = 'auth-user';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  constructor(private http: HttpClient) {}

  clean(): void {
    window.sessionStorage.clear();
  }

  public saveUser(user: any): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  public isLoggedIn(): boolean {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }

  private profileUrl = 'http://localhost:8081/api/user/profile';
  getUserProfile(): Observable<any> {
    const token = this.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.get(this.profileUrl, { headers });
  }

  private updateProfileUrl = 'http://localhost:8081/api/user/update';
  updateUserProfile(university: string, faculty: string, dateOfBirth: string, cnp: string): Observable<any> {
    const token = this.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(
      this.updateProfileUrl,
      {
        university,
        faculty,
        dateOfBirth,
        cnp
      },
      {
        headers: headers,
        responseType: 'text'
      }
    );
  }
}