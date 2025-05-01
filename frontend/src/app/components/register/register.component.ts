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
    dateOfBirth: null,
    cnp: null,
    university: null,
    faculty: null,
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

  constructor(private authService: AuthService) { }

  onSubmit(): void {
    const { firstName, lastName, email, registrationNumber, dateOfBirth, cnp, university, faculty, password, confirmationPassword } = this.form;

    // Check if passwords match
    if (!this.passwordsMatch()) {
      this.errorMessage = "Passwords do not match!";
      this.isSignUpFailed = true;
      return; // Stop the submission
    }

    let formattedDateOfBirth = dateOfBirth;
    if (dateOfBirth instanceof Date) {
      formattedDateOfBirth = dateOfBirth.toISOString().split('T')[0];
    }
    else if (typeof dateOfBirth === 'string' && dateOfBirth.includes('T')) {
      // If it's an ISO string, extract just the date part
      formattedDateOfBirth = dateOfBirth.split('T')[0];
    }
    
    console.log('Sending dateOfBirth:', formattedDateOfBirth);

    this.authService.register(firstName, lastName, email, registrationNumber, formattedDateOfBirth, cnp, university, faculty, password, confirmationPassword).subscribe({
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