import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsService } from '../_services/forms.service';
import { FormField } from '../models/form.model';
import { StorageService } from '../_services/storage.service';
import { environment } from '../../../environments/environments';
import { Router } from '@angular/router';

@Component({
  selector: 'app-complete-form',
  standalone: false,
  templateUrl: './complete-form.component.html',
  styleUrl: './complete-form.component.css'
})
export class CompleteFormComponent {
  selectedFormId: number | null = null;
  formFields: FormField[] = [];
  warningMessage: string = '';
  isLoading: boolean = false;

  constructor(private route: ActivatedRoute, private formsService: FormsService, private router: Router) {}
  imageUrl = `${environment.backendUrl}/api/forms`;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.selectedFormId = id ? +id : null;
      this.imageUrl = this.imageUrl + '/' + this.selectedFormId + '/image';
      this.fetchForm(this.selectedFormId);
    });
  }

  fetchForm(id: number | null): void {
  if (id) {
    this.formsService.getFormFieldsById(id).subscribe(
      (data: any) => {
        // console.log('Form fields:', data);
        this.formFields = data.fields;
      },
      (error) => {
        console.error('Error fetching form:', error);
      }
    );
  } else {
    console.error('Invalid form ID');
  }
}


  getFormFields(): FormField[] {
    return this.formFields;
  }

  saveFormFields() {
    if (!this.allFieldsCompleted()) {
      this.warningMessage = 'Te rugăm să completezi toate câmpurile!';
      return;
    }
    this.warningMessage = '';
    this.isLoading = true;
    // Get only the values entered by the user (not the whole field object)
    const values = this.formFields
      .filter(field => field.value && field.value.trim() !== '')
      .map(field => field.value);
    console.log('User inputted values:', values);
    // Now you have an array of just the inputted values
    this.formsService.submitFormData(this.selectedFormId!, values).subscribe(
      (response) => {
        console.log('Form submitted successfully:', response);
        this.isLoading = false;
        this.router.navigate([`/student/submitted-forms`]);
      },
      (error) => {
        console.error('Error submitting form:', error);
        this.isLoading = true;
      }
    );
  }

  allFieldsCompleted(): boolean {
    return this.formFields.every(field => field.value && field.value.trim() !== '');
  }
}
