import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; 

@Component({
  standalone: true,
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
  imports: [CommonModule, FormsModule]
})
export class AccountComponent {
  isProfileComplete: boolean = false;
  isFormSubmitting: boolean = false;
  isUpdateSuccessful: boolean = false;
  errorMessage: string = '';
  additionalForm = {
    university: '',
    faculty: '',
    dateOfBirth: '',
    cnp: ''
  };
  currentUser = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    registrationNumber: '123456',
    university: 'Example University',
    faculty: 'Computer Science',
    dateOfBirth: '1990-01-01',
    cnp: '1234567890123'
  };

  requests: { id: number; formName: string; status: string }[] = [];
  currentUserId: string = ''; 

  constructor(private router: Router) {} 

  onSubmit(): void {
    this.isFormSubmitting = true;
    setTimeout(() => {
      this.isFormSubmitting = false;
      this.isUpdateSuccessful = true;
    }, 1000);
  }

  logout(): void {
    this.requests = [];
    this.currentUserId = '';
    localStorage.removeItem(`requests_${this.currentUserId}`);
    this.router.navigate(['/login']);
  }
}