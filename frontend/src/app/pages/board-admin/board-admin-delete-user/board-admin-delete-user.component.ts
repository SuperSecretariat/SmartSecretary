import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { StorageService } from '../../../components/_services/storage.service';

@Component({
  selector: 'app-board-admin-delete-user',
  standalone: false,
  templateUrl: './board-admin-delete-user.component.html',
  styleUrl: './board-admin-delete-user.component.css'
})
export class BoardAdminDeleteUserComponent {
  @ViewChild('formDeleteUser') formDeleteUser!: NgForm;
  
  formData: any = {
    registrationNumber: null
  };
  
  isSuccessful = false;
  errorMessage = '';
  showDeleteModal = false;
  isDeletingUser = false;

  constructor(private readonly storageService: StorageService) { }

  onSubmit(): void {
    // This method is now only used for form validation
    // Actual deletion happens in deleteUser() after confirmation
    if (this.formDeleteUser.form.valid) {
      this.showDeleteConfirmation();
    }
  }

  showDeleteConfirmation(): void {
    if (!this.formData.registrationNumber) {
      return;
    }
    this.showDeleteModal = true;
    document.body.classList.add('modal-open');
  }

  hideDeleteConfirmation(): void {
    this.showDeleteModal = false;
    document.body.classList.remove('modal-open');
  }

  deleteUser(): void {
    this.isDeletingUser = true;
    this.errorMessage = '';
    
    const { registrationNumber } = this.formData;

    this.storageService.deleteUserAccount(registrationNumber).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessful = true;
        this.isDeletingUser = false;
        this.hideDeleteConfirmation();
        this.formDeleteUser.resetForm();
      },
      error: err => {
        console.log('Delete error:', err);
        this.errorMessage = err.error?.message ?? 'Error deleting user';
        this.isSuccessful = false;
        this.isDeletingUser = false;
        this.hideDeleteConfirmation();
      }
    });
  }
}