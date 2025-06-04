import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsService } from '../_services/forms.service';
import { FormField } from '../models/form-field.model';
import { environment } from '../../../environments/environments';
import { FormPage } from '../models/form-page.model';
import { firstValueFrom, Observable, of, tap } from 'rxjs';

@Component({
  selector: 'app-complete-form',
  standalone: false,
  templateUrl: './complete-form.component.html',
  styleUrl: './complete-form.component.css'
})
export class CompleteFormComponent implements OnInit {
  selectedFormId: number | null = null;
  formFields: FormField[] = [];
  warningMessage: string = '';
  isLoading: boolean = false;
  formImagesUrl: string[] = [];
  pages: FormPage[] = [];

  constructor(private readonly route: ActivatedRoute, private readonly formsService: FormsService, private readonly router: Router) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      this.selectedFormId = id ? +id : null;
      await firstValueFrom(this.fetchFormImages(this.selectedFormId));
      await firstValueFrom(this.fetchForm(this.selectedFormId));
      this.groupFieldsByPage();
    });
  }

  fetchForm(id: number | null): Observable<any> {
    if (id) {
      return this.formsService.getFormFieldsById(id).pipe(
        tap((data: any) => {
          this.formFields = data.fields;
          console.log('Form fields:', this.formFields);
        })
      );
    }
    else {
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

  groupFieldsByPage() {
    const pagesMap = new Map<number, FormField[]>();
    console.log(this.formFields);
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
    // Now you have an array of just the inputted values
    this.formsService.submitFormData(this.selectedFormId!, values).subscribe({
      next: _ => {
        this.isLoading = false;
        this.router.navigate([`/student/submitted-forms`]);
      },
      error: (error) => {
        console.error('Error submitting form:', error);
        this.isLoading = true;
      }
    });
  }

  allFieldsCompleted(): boolean {
    return this.formFields.every(field => field.value && field.value.trim() !== '');
  }
}
