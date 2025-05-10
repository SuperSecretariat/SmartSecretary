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
    emailAdmin: null,
    authKeyAdmin: null
  };
  formSecretaryData: any = {
    emailSecretary: null,
    authKeySecretary: null
  };
  content?: string;
  isSuccessfulAdmin = false;
  isSuccessfulSecretary = false;
  errorMessage = '';


  constructor(private readonly userService: UserService, private readonly addService: AddService) { }

  success():void{
    this.isSuccessfulAdmin = false;
    this.isSuccessfulSecretary = false;
  }

  onSubmitAdmin(): void {
    const { emailAdmin, authKeyAdmin } = this.formAdminData;

    this.addService.addAdmin(emailAdmin, authKeyAdmin).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessfulAdmin = true;
        this.formAdmin.resetForm();
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
        this.isSuccessfulAdmin = false;
      }
    });

  }

  onSubmitSecretary(): void {
    const { emailSecretary, authKeySecretary } = this.formSecretaryData;

    this.addService.addSecretary(emailSecretary, authKeySecretary).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessfulSecretary = true;
        this.formSecretary.resetForm();
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
        this.isSuccessfulSecretary = false;
      }
    });
  }
}
