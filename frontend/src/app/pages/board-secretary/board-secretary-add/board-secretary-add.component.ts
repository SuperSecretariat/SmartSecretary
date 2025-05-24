import {Component, ViewChild} from '@angular/core';
import {AddService} from '../../../components/_services/add.service';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-board-secretary-add',
  standalone: false,
  templateUrl: './board-secretary-add.component.html',
  styleUrl: './board-secretary-add.component.css'
})
export class BoardSecretaryAddComponent{
  @ViewChild('form') form!: NgForm;
  formData: any = {
    registrationNumber: null,
    email: null
  };

  constructor(private readonly addService: AddService) {
  }

  content?: string;
  isSuccessful = false;
  errorMessage = '';

  onSubmit(): void {
    const { registrationNumber, email } = this.formData;

    this.addService.addStudent(registrationNumber, email).subscribe({
      next: data => {
        console.log(data);
        this.form.resetForm();
        this.isSuccessful = true;
      },
      error: err => {
        console.log('Add error:', err);
        this.errorMessage = err.error.message;
      }
    });
  }

}
