import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsService } from '../_services/forms.service';
import { FormField } from '../models/form.model';

@Component({
  selector: 'app-complete-form',
  standalone: false,
  templateUrl: './complete-form.component.html',
  styleUrl: './complete-form.component.css'
})
export class CompleteFormComponent {
  selectedFormId: number | null = null;
  formFields: FormField[] = [];

  constructor(private route: ActivatedRoute, private formsService: FormsService) {}
  imageUrl = `http://localhost:8081/api/forms`;

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
        console.log('Form fields:', data); // Verifică structura datelor
        this.formFields = data.fields; // Accesează direct proprietatea `fields`
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
  // Get only the values entered by the user (not the whole field object)
  const values = this.formFields
    .filter(field => field.value && field.value.trim() !== '')
    .map((field, index) => ({
      id: 0,
      data: field.value
    }));
  console.log('User inputted values:', values);
  // Now you have an array of just the inputted values
  this.formsService.submitFormData('123', this.selectedFormId!, values).subscribe(
    (response) => {
      console.log('Form submitted successfully:', response);
    },
    (error) => {
      console.error('Error submitting form:', error);
    }
  );
}
}
