import { Component } from '@angular/core';
import { AuthService } from '../_services/auth.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: false
})
export class RegisterComponent {
  form: any = {
    firstName: null,
    lastName: null,
    email: null,
    registrationNumber: null,
    password: null,
    confirmationPassword: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  passwordsMatch(): boolean {
    const { password, confirmationPassword } = this.form;
    return password === confirmationPassword;
  }

  passwordValid(password: string): boolean {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(password);
  }

  constructor(private readonly authService: AuthService) { }

  onSubmit(): void {
    const { firstName, lastName, email, registrationNumber, password, confirmationPassword } = this.form;

    if (!this.passwordsMatch()) {
      this.errorMessage = "Passwords do not match!";
      this.isSignUpFailed = true;
      return;
    }

    if (!this.passwordValid(password)) {
      this.errorMessage = "Password must be at least 8 characters, include upper and lower case letters, a number, and a special character.";
      this.isSignUpFailed = true;
      return;
    }

    this.authService.register(firstName, lastName, email, registrationNumber, password, confirmationPassword).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      error: err => {
        console.log('Registration error:', err);
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    });
  }
}