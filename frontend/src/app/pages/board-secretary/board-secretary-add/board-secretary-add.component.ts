import {Component} from '@angular/core';
import {AddService} from '../../../components/_services/add.service';

@Component({
  selector: 'app-board-secretary-add',
  standalone: false,
  templateUrl: './board-secretary-add.component.html',
  styleUrl: './board-secretary-add.component.css'
})
export class BoardSecretaryAddComponent{
  form: any = {
    registrationNumber: null,
    email: null
  };

  constructor(private readonly addService: AddService) {
  }

  content?: string;
  isSuccessful = false;
  errorMessage = '';

  onSubmit(): void {
    const { registrationNumber, email } = this.form;

    this.addService.addStudent(registrationNumber, email).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessful = true;
        this.form.resetForm();
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
      }
    });
  }

}
