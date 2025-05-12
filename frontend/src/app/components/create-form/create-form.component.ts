import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-create-form',
  templateUrl: './create-form.component.html',
  styleUrls: ['./create-form.component.css'],
  imports: [CommonModule, FormsModule]
})
export class CreateFormComponent {
  forms = ['Form1', 'Form2', 'Form3']; 
  selectedForm: string | null = null; 
  requests: { id: number; formName: string; status: string }[] = [];
  submittedRequests: { id: number; formName: string; status: string }[] = []; 
  currentUserId: string = ''; 
  selectedRequests: { id: number; formName: string; status: string }[] = [];

  constructor(private router: Router) {
    this.currentUserId = this.getLoggedInUserId();

    const storedRequests = localStorage.getItem(`requests_${this.currentUserId}`);
    if (storedRequests) {
      this.requests = JSON.parse(storedRequests);
    }

    const storedSubmittedRequests = localStorage.getItem(`submitted_requests_${this.currentUserId}`);
    if (storedSubmittedRequests) {
      this.submittedRequests = JSON.parse(storedSubmittedRequests);
    }
  }

  getLoggedInUserId(): string {
    return 'user123';
  }

  selectForm(form: string): void {
    this.selectedForm = form;
  }

  addRequest(): void {
    if (this.selectedForm) {
      const allRequests = [...this.requests, ...this.submittedRequests];
      const maxId = allRequests.length > 0 ? Math.max(...allRequests.map(r => r.id)) : 0;

      const newRequest = {
        id: maxId + 1,
        formName: this.selectedForm,
        status: 'Sent'
      };

      this.requests.push(newRequest);
      localStorage.setItem(`requests_${this.currentUserId}`, JSON.stringify(this.requests));

      alert(`The request for ${this.selectedForm} has been added with the number ${newRequest.id}!`);
      this.selectedForm = null;
    }
  }

  goToHome(): void {
    this.router.navigate(['/submitted-forms']);
  }

  toggleRequestSelection(request: { id: number; formName: string; status: string }): void {
    const index = this.selectedRequests.findIndex(r => r.id === request.id);
    if (index === -1) {
      this.selectedRequests.push(request);
    } else {
      this.selectedRequests.splice(index, 1);
    }
  }

  submitRequests(): void {
    this.selectedRequests.forEach(request => {
      request.status = 'Sent';
      this.submittedRequests.push(request);
    });

    localStorage.setItem(`submitted_requests_${this.currentUserId}`, JSON.stringify(this.submittedRequests));
    this.requests = this.requests.filter(request => !this.selectedRequests.includes(request));
    this.selectedRequests = [];
    localStorage.setItem(`requests_${this.currentUserId}`, JSON.stringify(this.requests));
    alert('The selected requests have been submitted!');
  }
}

