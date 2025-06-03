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
  templateUrl: './modify-form.component.html',
  styleUrl: './modify-form.component.css'
})
export class ModifyFormComponent {
  selectedFormRequestId: number | null = null;
  selectedFormId: number | null = null;
  formFields: FormField[] = [];
  warningMessage: string = '';
  isLoading: boolean = false;
  formRequestFields: string[] = [];

  constructor(private route: ActivatedRoute, private formsService: FormsService, private router: Router) { }
  imageUrl = `${environment.backendUrl}/api/forms`;

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      const formId = params.get('formId');
      this.selectedFormRequestId = id ? +id : null;
      this.selectedFormId = formId ? +formId : null;
      this.fetchFormRequestFields(this.selectedFormRequestId);
      this.fetchForm(this.selectedFormId);
      this.loadFormRequestFieldsValueIntoFormFields();
      this.imageUrl = this.imageUrl + '/' + this.selectedFormId + '/image';
    });
  }

  loadFormRequestFieldsValueIntoFormFields(){
    console.log('Loading form request fields into form fields', this.formRequestFields);
    let i = 0;
    for (const field of this.formFields) {
      if (this.formRequestFields[i]) {
        field.value = this.formRequestFields[i];
      } else {
        field.value = ''; // or any default value you want
      }
      i++;
    }
  }

  fetchForm(id: number | null): void {
    console.log('Fetching form with ID:', id);
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
      console.error('Invalid form IDsss');
    }
  }

  fetchFormRequestFields(id: number | null) {
    if (id) {
      this.formsService.getFormRequestFieldsById(id).subscribe(
        (data: any) => {
          //console.log('Form request fields:', data);
          this.formRequestFields = data.fieldsData;
          console.log('Form request fields:', this.formRequestFields);
          // Initialize values for each field
          this.formFields.forEach(field => {
            field.value = ''; // or any default value you want
          });
        },
        (error) => {
          console.error('Error fetching form request fields:', error);
        }

      );
    } else {
      console.error('Invalid form request ID');
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
    //console.log('User inputted values:', values);
    // Now you have an array of just the inputted values
    this.formsService.submitFormData(this.selectedFormId!, values).subscribe(
      (response) => {
        //console.log('Form submitted successfully:', response);
        this.isLoading = false;
        this.router.navigate([`/student/submitted-forms`]);
      },
      (error) => {
        console.error('Error submitting form:', error);
        this.isLoading = true;
      }
    );
    this.formsService.deleteFormRequest(this.selectedFormRequestId!).subscribe(
      (response) => {
        //console.log('Form request deleted successfully:', response);
      },
      (error) => {
        console.error('Error deleting form request:', error)
      }
      );
  }

  allFieldsCompleted(): boolean {
    return this.formFields.every(field => field.value && field.value.trim() !== '');
  }
}
