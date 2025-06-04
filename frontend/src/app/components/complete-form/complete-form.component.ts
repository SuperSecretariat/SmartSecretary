import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsService } from '../_services/forms.service';
import { FormField } from '../models/form-field.model';
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
  profileSuggestion: string = '';

  constructor(private readonly route: ActivatedRoute, private readonly formsService: FormsService, private readonly router: Router) { }

  fieldTouched: boolean[] = [];
  userProfile: any = null;
  validationErrors: string[] = [];

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      this.selectedFormId = id ? +id : null;
      await firstValueFrom(this.fetchFormImages(this.selectedFormId));
      await firstValueFrom(this.fetchForm(this.selectedFormId));
      this.groupFieldsByPage();
    });
  }

  getGlobalIndex(pageIndex: number, fieldIndex: number): number {
  let count = 0;
  for (let i = 0; i < pageIndex; i++) {
    count += this.pages[i].inputFields.length;
  }
  return count + fieldIndex;
}


  fetchForm(id: number | null): Observable<any> {
    if (id) {
      return this.formsService.getFormFieldsById(id).pipe(
        tap((data: any) => {
          this.formFields = data.fields.sort((a: FormField, b: FormField) => {
            const pageA = parseInt(a.page || '1');
            const pageB = parseInt(b.page || '1');
            if (pageA !== pageB) return pageA - pageB;

            const topA = parseFloat(a.top || '0');
            const topB = parseFloat(b.top || '0');
            return topA - topB;
          });

          this.fieldTouched = new Array(this.formFields.length).fill(false);

          this.formsService.getUserProfile().subscribe(
            profile => {
              this.userProfile = profile;
              this.autocompleteFromProfile();
            },
            error => {
              console.warn('Profilul nu a putut fi încărcat sau nu este complet.', error);
            }
          );
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
autocompleteFromProfile(): void {
    if (!this.userProfile) return;

    const profile = this.userProfile;
    const missingFields: string[] = [];

    if (!profile.cnp) missingFields.push('CNP');
    if (!profile.university) missingFields.push('Universitate');
    if (!profile.faculty) missingFields.push('Facultate');
    if (!profile.registrationNumber) missingFields.push('Număr matricol');
    if (!profile.dateOfBirth) missingFields.push('Data nașterii');

    if (missingFields.length > 0) {
      this.profileSuggestion = `Recomandare: Completează profilul tău (lipsesc: ${missingFields.join(', ')}) pentru a beneficia de completarea automată a formularului.`;
    }

    this.formFields.forEach((field, i) => {
      const label = field.label?.toLowerCase() || '';
      const prevWord = field.previousWord?.toLowerCase() || '';
      const beforePrev = i >= 1 ? this.formFields[i - 1].previousWord?.toLowerCase() || '' : '';

      if ((label.includes('nume') || label.includes('subsemnat') || prevWord === '(a)') && !field.value) {
        field.value = `${profile.firstName} ${profile.lastName}`;
      }

      if (label.includes('email') && !field.value) {
        field.value = profile.email;
      }

      if (label.includes('cnp') && !field.value) {
        field.value = profile.cnp;
      }

      if (label.includes('matricol') && !field.value) {
        field.value = profile.registrationNumber;
      }

      if (label.includes('universitate') && !field.value) {
        field.value = profile.university;
      }

      if (label.includes('facultate') && !field.value) {
        field.value = profile.faculty;
      }

      if ((label.includes('naștere') || label.includes('nastere')) && !field.value && profile.dateOfBirth) {
        const dob = new Date(profile.dateOfBirth);
        field.value = `${dob.getDate().toString().padStart(2, '0')}.${(dob.getMonth() + 1).toString().padStart(2, '0')}.${dob.getFullYear()}`;
      }

      if (!field.value && (label.includes('dată') || label.includes('data'))) {
        const today = new Date();
        const formattedDate = `${today.getDate().toString().padStart(2, '0')}.${(today.getMonth() + 1).toString().padStart(2, '0')}.${today.getFullYear()}`;
        field.value = formattedDate;
      }

    });
  }

  dismissSuggestion(): void {
    this.profileSuggestion = '';  
}

  getFormFields(): FormField[] {
    return this.formFields;
  }

  getFieldValidationError(field: FormField): string | null {
    const label = field.label?.toLowerCase() || '';
    const value = field.value?.trim() || '';
    const currentYear = new Date().getFullYear();

    if (!value) return null;

    if (label.includes('nume') || label.includes('subsemnat')) {
      if (!/^([A-ZȘȚĂÎÂ][a-zșțăîâ]+([- ][A-ZȘȚĂÎÂ][a-zșțăîâ]+)+)$/.test(value)) {
        return 'Numele complet trebuie să conțină cel puțin două cuvinte și să înceapă cu literă mare.';
      }
    }

    if (label.includes('promo')) {
      const promo = Number(value);
      if (isNaN(promo) || promo < 1900 || promo > currentYear) {
        return `Promoția trebuie să fie între 1900 și ${currentYear}.`;
      }
    }

    if (label.includes('specializ')) {
      if (!/^[A-ZȘȚĂÎÂ][a-zșțăîâ]+(?: [A-ZȘȚĂÎÂa-zșțăîâ]+)*$/.test(value)) {
        return 'Specializarea trebuie să înceapă cu literă mare.';
      }
    }

    if (label.includes('perioad')) {
      if (!/^\d{4}-\d{4}$/.test(value)) {
        return 'Perioada de studii trebuie să fie în formatul 20XX-20XX.';
      }
    }

    if (label.includes('an de studii')) {
      const an = Number(value);
      if (isNaN(an) || an < 1 || an > 3) {
        return 'Anul de studii trebuie să fie între 1 și 3.';
      }
    }

    if (label.includes('semestr')) {
      const sem = Number(value);
      if (isNaN(sem) || sem < 1 || sem > 2) {
        return 'Semestrul trebuie să fie 1 sau 2.';
      }
    }

    if (label.includes('cnp')) {
      if (!/^\d{13}$/.test(value)) {
        return 'CNP-ul trebuie să conțină exact 13 cifre.';
      }
    }

    if (label.includes('matricol')) {
      if (!/^[a-zA-Z0-9]{10,}$/.test(value)) {
        return 'Numărul matricol trebuie să aibă cel puțin 10 caractere alfanumerice.';
      }
    }

    if (label.includes('disciplina')) {
      if (!/^[A-ZȘȚĂÎÂ][a-zșțăîâ]*( [a-zA-ZȘȚĂÎÂșțăîâ0-9]+)*$/.test(value)) {
        return 'Numele disciplinei trebuie să înceapă cu literă mare.';
      }
      if (value.length < 2) {
        return 'Numele disciplinei trebuie să aibă cel puțin 2 caractere.';
      }
    }

    if (label.includes('titular') || label.includes('prof.') || label.includes('conf.') || label.includes('lect.') || label.includes('asist.') || label.includes('profesor')) {
      if (!/^([A-ZȘȚĂÎÂ][a-zșțăîâ]+([- ][A-ZȘȚĂÎÂ][a-zșțăîâ]+)+)$/.test(value)) {
        return 'Numele profesorului trebuie să fie complet și corect scris.';
      }
    }

    if (label.includes('email')) {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return 'Adresa de email este invalidă.';
      }
    }

    if (label.includes('telefon')) {
      if (!/^07\d{8}$/.test(value)) {
        return 'Numărul de telefon trebuie să înceapă cu 07 și să aibă exact 10 cifre.';
      }
    }

    if (label.includes('dată') || label.includes('data')) {
      if (!/^\d{2}\.\d{2}\.\d{4}$/.test(value)) {
        return 'Data trebuie să fie în formatul dd.mm.yyyy.';
      }
    }

    if (label.includes('an universitar')) {
      const parts = value.split('-');
      const y1 = parseInt(parts[0]), y2 = parseInt(parts[1]);
      const currentYear = new Date().getFullYear();

      if (parts.length !== 2 || isNaN(y1) || isNaN(y2) || y2 !== y1 + 1) {
        return 'Anul universitar este invalid. Format corect: 2024-2025.';
      }

      if (y1 > currentYear) {
        return 'Anul universitar nu poate fi în viitor.';
      }
    }

    if (label.includes('nr.')) {
      if (!/^\d+$/.test(value)) {
        return 'Câmpul „Nr.” trebuie să conțină doar cifre.';
      }
    }

    if (label.includes('anul')) {
      const year = Number(value);
      if (isNaN(year) || year < 1900 || year > currentYear) {
        return 'Valoarea pentru „Anul” trebuie să fie un an valid.';
      }
    }

    if (label.includes('pentru')) {
      if (value.length < 3) {
        return 'Valoarea „pentru” trebuie să fie mai lungă.';
      }
    }

    return null;
  }

  isFieldValid(field: FormField, index?: number): boolean {
    const value = field.value?.trim() || '';

    if (!value) {
      return !(typeof index === 'number' && this.fieldTouched[index]);
    }

    return this.getFieldValidationError(field) === null;
  }

  allFieldsCompleted(): boolean {
    return this.formFields.every(field => field.value && field.value.trim() !== '');
  }

  validateSpecificFields(): boolean {
    this.validationErrors = [];

    for (const field of this.formFields) {
      const error = this.getFieldValidationError(field);
      if (error) {
        this.validationErrors.push(error);
      }
    }

    return this.validationErrors.length === 0;
  }

  saveFormFields() {
    this.validationErrors = [];

    if (!this.allFieldsCompleted()) {
      this.warningMessage = 'Te rugăm să completezi toate câmpurile!';
      return;
    }

    if (!this.validateSpecificFields()) {
      this.warningMessage = '';
      return;
    }

    this.warningMessage = '';
    this.isLoading = true;

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
}
