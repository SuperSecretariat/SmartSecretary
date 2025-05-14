import { Component } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
  standalone: false
})
export class ForgotPasswordComponent {
  form: any = {
    email: null
  };
  isSubmitted = false;
  isSubmitFailed = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) { }

  onSubmit(): void {
    const { email } = this.form;

    this.authService.forgotPassword(email).subscribe({
      next: data => {
        this.isSubmitted = true;
        this.isSubmitFailed = false;
        this.successMessage = "Password reset instructions have been sent to your email.";
      },
      error: err => {
        this.errorMessage = err.error.message ?? "Error processing your request.";
        this.isSubmitFailed = true;
      }
    });
  }

  returnToLogin(): void {
    this.router.navigate(['/login']);
  }
}