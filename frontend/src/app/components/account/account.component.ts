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
  isEditMode = false; // New property to track edit mode

  additionalForm: any = {
    university: null,
    faculty: null,
    dateOfBirth: null,
    cnp: null
  };
  
  constructor(private readonly storageService: StorageService) { }
  
  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.storageService.getUserProfile().subscribe({
      next: data => {
        this.currentUser = data;
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
    this.errorMessage = '';
    this.isUpdateSuccessful = false;
    
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
        this.isEditMode = false;
        this.storageService.saveUser(this.currentUser);
      },
      error: (err) => {
        this.errorMessage = err.error.message ?? 'Error updating profile';
        this.isFormSubmitting = false;
        this.isUpdateSuccessful = false;
      }
    });
  }

  editProfile(): void {
    this.isEditMode = true;
    this.errorMessage = '';
    this.isUpdateSuccessful = false;
    
    this.additionalForm = {
      university: this.currentUser.university,
      faculty: this.currentUser.faculty,
      dateOfBirth: this.currentUser.dateOfBirth,
      cnp: this.currentUser.cnp
    };
  }

  cancelEdit(): void {
    this.isEditMode = false;
    this.errorMessage = '';
    this.isUpdateSuccessful = false;
  }
}