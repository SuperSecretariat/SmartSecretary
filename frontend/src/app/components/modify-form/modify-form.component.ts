import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsService } from '../_services/forms.service';
import { FormField } from '../models/form-field.model';
import { environment } from '../../../environments/environments';
import { firstValueFrom, Observable, of, tap } from 'rxjs';
import { FormPage } from '../models/form-page.model';

@Component({
  selector: 'app-complete-form',
  standalone: false,
  templateUrl: './modify-form.component.html',
  styleUrl: './modify-form.component.css'
})
export class ModifyFormComponent implements OnInit {
  selectedFormRequestId: number | null = null;
  selectedFormId: number | null = null;
  formFields: FormField[] = [];
  warningMessage: string = '';
  isLoading: boolean = false;
  formRequestFields: string[] = [];
  pages: FormPage[] = [];
  formImagesUrl: string[] = [];

  constructor(private readonly route: ActivatedRoute, private readonly formsService: FormsService, private readonly router: Router) { }
  imageUrl = `${environment.backendUrl}/api/forms`;

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      const formId = params.get('formId');
      this.selectedFormRequestId = id ? +id : null;
      this.selectedFormId = formId ? +formId : null;
      await firstValueFrom(this.fetchFormImages(this.selectedFormId))
      await firstValueFrom(this.fetchFormRequestFields(this.selectedFormRequestId));
      await firstValueFrom(this.fetchForm(this.selectedFormId));
      this.loadFormRequestFieldsValueIntoFormFields();
      this.groupFieldsByPage();
    });
  }

  loadFormRequestFieldsValueIntoFormFields() {
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

  fetchForm(id: number | null): Observable<any> {
    if (id) {
      return this.formsService.getFormFieldsById(id).pipe(
        tap((data: any) => {
          this.formFields = data.fields;
          console.log('Form fields:', this.formFields);
        })
      );
    } else {
      console.error('Invalid form ID');
      return of(null);
    }
  }

  fetchFormImages(id: number | null): Observable<any> {
    if (id) {
      return this.formsService.getFormImages(id).pipe(
        tap((data: string[]) => {
          this.formImagesUrl = data.map(base64 => 'data:image/png;base64,' + base64);
        })
      );   
    } else {
      console.log('Invalid form ID');
      return of(null);
    }  
  }

  fetchFormRequestFields(id: number | null): Observable<any> {
    if (id) {
      return this.formsService.getFormRequestFieldsById(id).pipe(
        tap((data: any) => {
          this.formRequestFields = data.fieldsData;
          this.formFields.forEach(field => {
            field.value = '';
          });
          console.log('Form request fields:', this.formRequestFields);
        })
      );
    } else {
      console.error('Invalid form request ID');
      return of(null); // return empty Observable to keep `await firstValueFrom()` safe
    }
  }

  groupFieldsByPage() {
    const pagesMap = new Map<number, FormField[]>();

    for (const field of this.formFields) {
      const page = parseInt(field.page as any);
      if (!pagesMap.has(page)) {
        pagesMap.set(page, []);
      }
      pagesMap.get(page)!.push(field);
    }

    this.pages = Array.from(pagesMap.entries())
      .sort(([a], [b]) => a - b)
      .map(([_, fields], index) => ({
        inputFields: fields,
        imageUrl: this.formImagesUrl[index]
      }));
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
    this.formsService.submitFormData(this.selectedFormId!, values).subscribe({
      next: _ => {
        this.isLoading = false;
        this.router.navigate([`/student/submitted-forms`]);
      },
      error: error => {
        console.error('Error submitting form:', error);
        this.isLoading = true;
      }
    });
    this.formsService.deleteFormRequestById(this.selectedFormRequestId!).subscribe({
      next: _ => { },
      error: error => {
        console.error('Error deleting form request:', error)
      }
    });
  }

  allFieldsCompleted(): boolean {
    return this.formFields.every(field => field.value && field.value.trim() !== '');
  }
}
