import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { CnpValidatorService } from '../_services/cnp-validator.service';
import { Router } from '@angular/router';

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
  isEditMode = false;

  showDeleteModal = false;
  isDeletingAccount = false;

  additionalForm: any = {
    university: null,
    faculty: null,
    dateOfBirth: null,
    cnp: null
  };
  
  constructor(
    private readonly storageService: StorageService,
    private readonly cnpValidator: CnpValidatorService,
    private readonly router: Router
  ) { }
  
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
    
    // Validate CNP before submitting
    const cnpValidation = this.cnpValidator.validateCnp(cnp);
    if (!cnpValidation.isValid) {
      this.errorMessage = cnpValidation.errorMessage ?? 'Invalid CNP';
      this.isFormSubmitting = false;
      return;
    }
    
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
  
  onDateOfBirthChange(): void {
    if (!this.additionalForm.dateOfBirth) return;
    
    const birthDate = new Date(this.additionalForm.dateOfBirth);
    const year = birthDate.getFullYear();
    const month = birthDate.getMonth() + 1; 
    const day = birthDate.getDate();
    
    let genderDigit: number;
    if (year < 2000) {
      genderDigit = 1;
    } else {
      genderDigit = 5;
    }
    
    const yearStr = (year % 100).toString().padStart(2, '0');
    const monthStr = month.toString().padStart(2, '0');
    const dayStr = day.toString().padStart(2, '0');

    const partialCnp = `${genderDigit}${yearStr}${monthStr}${dayStr}`;
    
    if (!this.additionalForm.cnp || this.additionalForm.cnp.length !== 13) {
      this.additionalForm.cnp = partialCnp;
    }
  }

  showDeleteConfirmation(): void {
    this.showDeleteModal = true;
    document.body.classList.add('modal-open');
  }

  hideDeleteConfirmation(): void {
    this.showDeleteModal = false;
    document.body.classList.remove('modal-open');
  }

  deleteAccount(): void {
    this.isDeletingAccount = true;
    this.errorMessage = '';

    this.storageService.deleteAccount().subscribe({
      next: () => {
        this.storageService.clean();
        this.isDeletingAccount = false;
        this.hideDeleteConfirmation();
        this.router.navigate(['/login'], { 
          queryParams: { 
            deleted: 'true' 
          }
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.message ?? 'Error deleting account';
        this.isDeletingAccount = false;
        this.hideDeleteConfirmation();
      }
    });
  }
}