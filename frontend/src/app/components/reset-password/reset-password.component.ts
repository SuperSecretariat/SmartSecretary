import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
  standalone: false
})
export class ResetPasswordComponent implements OnInit {
  form: any = {
    newPassword: null,
    confirmPassword: null
  };
  token: string = '';
  isSubmitted = false;
  isSubmitFailed = false;
  errorMessage = '';
  passwordMismatch = false;

  constructor(
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] ?? '';
      if (!this.token) {
        this.errorMessage = 'Invalid or missing reset token';
        this.isSubmitFailed = true;
      }
    });
  }

  passwordValid(password: string): boolean {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(password);
  }

  onSubmit(): void {
    const { newPassword, confirmPassword } = this.form;

    if (newPassword !== confirmPassword) {
      this.passwordMismatch = true;
      return;
    }

    if (!this.passwordValid(newPassword)) {
      this.errorMessage = "Password must be at least 8 characters, include upper and lower case letters, a number, and a special character.";
      this.isSubmitFailed = true;
      return;
    }

    this.passwordMismatch = false;

    this.authService.resetPassword(this.token, newPassword).subscribe({
      next: data => {
        this.isSubmitted = true;
        this.isSubmitFailed = false;
      },
      error: err => {
        this.errorMessage = err.error.message ?? "Error resetting password.";
        this.isSubmitFailed = true;
      }
    });
  }

  returnToLogin(): void {
    this.router.navigate(['/login']);
  }
}