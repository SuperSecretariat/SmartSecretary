import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';

const USER_KEY = 'auth-user';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  constructor(private readonly http: HttpClient) { }

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

  public getRegistrationNumber(): string {
    return this.getUser().registrationNumber.toString();
  }

  public isLoggedIn(): boolean {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }

  public isAdmin(): boolean {
    const user = this.getUser();
    if (user && Array.isArray(user.roles)) {
      return user.roles.includes('ROLE_ADMIN');
    }
    return false;
  }

  public isSecretary(): boolean {
    const user = this.getUser();
    if (user && Array.isArray(user.roles)) {
      return user.roles.includes('ROLE_SECRETARY');
    }
    return false;
  }

  public isStudent(): boolean {
    const user = this.getUser();
    if (user && Array.isArray(user.roles)) {
      return user.roles.includes('ROLE_STUDENT');
    }
    return false;
  }

  private readonly profileUrl = `${environment.backendUrl}/api/user/profile`;
  getUserProfile(): Observable<any> {
    const token = this.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.get(this.profileUrl, { headers });
  }

  private readonly updateProfileUrl = `${environment.backendUrl}/api/user/update`;
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

  private readonly DELETE_API = `${environment.backendUrl}/api/user/delete-me`;
  deleteAccount(): Observable<any> {
    const token = this.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(this.DELETE_API, {},
      {
        headers: headers,
        responseType: 'text'
      });
  }

  private readonly SHOWAUTHKEY_API = `${environment.backendUrl}/api/user/authkey`;
  showAuthKey(email: string): Observable<any> {
    const token = this.getUser().token;
    const params = new HttpParams().set('email', email);
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.get(
      this.SHOWAUTHKEY_API,
      {
        headers: headers,
        params: params
      });
  }

  private readonly DELETEUSER_API = `${environment.backendUrl}/api/user/delete-user`;
  deleteUserAccount(registrationNumber: string): Observable<any> {
    const token = this.getUser().token;
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    return this.http.post(
      `${this.DELETEUSER_API}?identifier=${encodeURIComponent(registrationNumber)}`,
      {},
      {
        headers: headers,
        responseType: 'text'
      });
  }
}
