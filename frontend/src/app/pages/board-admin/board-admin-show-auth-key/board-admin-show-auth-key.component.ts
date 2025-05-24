import {Component} from '@angular/core';
import {NgForm} from '@angular/forms';
import { StorageService } from '../../../components/_services/storage.service';

@Component({
  selector: 'app-board-admin-show-auth-key',
  standalone: false,
  templateUrl: './board-admin-show-auth-key.component.html',
  styleUrl: './board-admin-show-auth-key.component.css'
})
export class BoardAdminShowAuthKeyComponent{
  form: any = {
    email: '',
  };
  errorMessage = '';
  authKey = null;
  success= false;

  constructor(private readonly storageService : StorageService) {
  }

  onSubmit(emailForm: NgForm): void {
    const { email } = this.form;

    this.storageService.showAuthKey(email).subscribe({
      next: data => {
        console.log(data);
        this.success = true;
        emailForm.resetForm();
        this.authKey=data.responseMessage;
      },
      error: err => {
        this.success = false;
        this.errorMessage = err.error.responseMessage;
        this.authKey = null;
      }
    });

  }
}
