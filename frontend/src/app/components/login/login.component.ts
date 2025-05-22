import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false
})
export class LoginComponent implements OnInit {
  form: any = {
    registrationNumber: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(
    private readonly authService: AuthService,
    private readonly storageService: StorageService,
    private readonly router: Router
  ) { }

  get formattedRoles(): string {
    if (!this.roles || this.roles.length === 0) {
      return 'User';
    }
    return this.roles.map(role => role.replace('ROLE_', '')).join(', ');
  }

  ngOnInit(): void {
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
    }
  }

  onSubmit(): void {
    const { registrationNumber, password } = this.form;

    this.authService.login(registrationNumber, password).subscribe({
      next: data => {
        this.storageService.saveUser(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = data.roles;
        const firstRole = this.roles[0];
        const rolePrefix = firstRole.replace('ROLE_', '').toLowerCase();
  
        this.router.navigate([`/${rolePrefix}/newsfeed`]).then(() => {
          window.location.reload();
        });
      },
      error: err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    });
  }

  reloadPage(): void {
    window.location.reload();
  }
}