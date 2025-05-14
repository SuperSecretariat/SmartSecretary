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
}
