import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrl: './account.component.css',
  standalone: false
})
export class AccountComponent implements OnInit{
  currentUser: any;
  isProfileComplete = false;
  isFormSubmitting = false;
  isUpdateSuccessful = false;
  errorMessage = '';

  additionalForm: any = {
    university: null,
    faculty: null,
    dateOfBirth: null,
    cnp: null
  };
  
    constructor(private readonly storageService: StorageService) { }
  
    ngOnInit(): void {
      this.storageService.getUserProfile().subscribe({
        next: data => {
          this.currentUser = data;
          console.log(this.isProfileComplete);
          console.log(this.currentUser.university);
          console.log(this.currentUser.faculty);
          console.log(this.currentUser.dateOfBirth);
          console.log(this.currentUser.cnp);
          this.isProfileComplete = (
            this.currentUser.university && 
            this.currentUser.faculty && 
            this.currentUser.dateOfBirth && 
            this.currentUser.cnp
          );
          if (this.isProfileComplete) {
            this.additionalForm = {
              university: this.currentUser.university,
              faculty: this.currentUser.faculty,
              dateOfBirth: this.currentUser.dateOfBirth,
              cnp: this.currentUser.cnp
            };
          }
        },
        error: err => console.error('Failed to fetch user profile', err)
      });
    }

    onSubmit(): void {
      this.isFormSubmitting = true;
      
      const { university, faculty, dateOfBirth, cnp } = this.additionalForm;
      
      this.storageService.updateUserProfile(university, faculty, dateOfBirth, cnp).subscribe({
        next: (response) => {
          this.isUpdateSuccessful = true;
          this.isFormSubmitting = false;
          this.currentUser = { 
            ...this.currentUser, 
            university, 
            faculty, 
            dateOfBirth, 
            cnp 
          };
          this.isProfileComplete = true;
          this.storageService.saveUser(this.currentUser);
        },
        error: (err) => {
          this.errorMessage = err.error.message ?? 'Error updating profile';
          this.isFormSubmitting = false;
          this.isUpdateSuccessful = false;
        }
      });
    }
}
