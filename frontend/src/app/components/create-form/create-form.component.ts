import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormsService } from '../_services/forms.service';
import { Form } from '../models/form.model';

@Component({
  standalone: true,
  selector: 'app-create-form',
  templateUrl: './create-form.component.html',
  styleUrls: ['./create-form.component.css'],
  imports: [CommonModule, FormsModule]
})
export class CreateFormComponent implements OnInit {
  forms: Form[] = [];

  selectedFormId: string = ''; 
  requests: { id: number; formName: string; status: string }[] = [];
  submittedRequests: { id: number; formName: string; status: string }[] = []; 
  currentUserId: string = ''; 
  selectedRequests: { id: number; formName: string; status: string }[] = [];

  constructor(private router: Router, private formsService: FormsService) {
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

  ngOnInit(): void {
    this.fetchForms();
  }

  fetchForms(): void {
    this.formsService.getAllForms().subscribe(
      (data: Form[]) => {
        this.forms = data;
      },
      (error) => {
        console.error('Error fetching forms:', error);
      }
    );
  }

  getLoggedInUserId(): string {
    return 'user123';
  }

  completeRequest(): void {
    if (this.selectedFormId == '') {
      alert('No form selected!');
      return;
    }      
    const selectedFormObject = this.forms.find(form => form.id == Number(this.selectedFormId));

      this.router.navigate([`/student/complete-form/${this.selectedFormId}`]);
      this.selectedFormId = '';
  }
}

