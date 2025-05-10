import {Component, ViewChild} from '@angular/core';
import {NgForm} from '@angular/forms';
import {UserService} from '../../../components/_services/user.service';
import {AddService} from '../../../components/_services/add.service';

@Component({
  selector: 'app-board-admin-add',
  standalone: false,
  templateUrl: './board-admin-add.component.html',
  styleUrl: './board-admin-add.component.css'
})
export class BoardAdminAddComponent{
  @ViewChild('formAdmin') formAdmin!: NgForm;
  @ViewChild('formSecretary') formSecretary!: NgForm;
  formAdminData: any = {
    email: null,
    authKey: null
  };
  formSecretaryData: any = {
    email: null,
    authKey: null
  };
  content?: string;
  isSuccessfulAdmin = false;
  isSuccessfulSecretary = false;
  errorMessage = '';


  constructor(private readonly userService: UserService, private readonly addService: AddService) { }

  onSubmitAdmin(): void {
    const { email, authKey } = this.formAdminData;

    this.addService.addAdmin(email, authKey).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessfulAdmin = true;
        this.formAdmin.resetForm();
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
        this.isSuccessfulAdmin = false;
        this.formAdmin.resetForm();
      }
    });
  }

  onSubmitSecretary(): void {
    const { email, authKey } = this.formSecretaryData;

    this.addService.addSecretary(email, authKey).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessfulSecretary = true;
        this.formSecretary.resetForm();
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
        this.isSuccessfulSecretary = false;
        this.formSecretary.resetForm();
      }
    });
  }
}
