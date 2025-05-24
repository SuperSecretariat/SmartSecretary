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
        //console.log('Forms fetched successfully:', this.forms);
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

    // if (!selectedFormObject) {
    //   alert('Form not found!');
    //   return;
    // }

    // const allRequests = [...this.requests, ...this.submittedRequests];
    // const maxId = allRequests.length > 0 ? Math.max(...allRequests.map(r => r.id)) : 0;

    // const newRequest = {
    //   id: maxId + 1,
    //   formName: selectedFormObject.title, 
    //   status: 'Sent'
    // };

      // this.requests.push(newRequest);
      // localStorage.setItem(`requests_${this.currentUserId}`, JSON.stringify(this.requests));

      // alert(`The request for "${selectedFormObject.title}" has been added with the number ${newRequest.id}!`);

      this.router.navigate([`/student/complete-form/${this.selectedFormId}`]);
      this.selectedFormId = '';
  }

  // goToHome(): void {
  //   this.router.navigate(['/submitted-forms']);
  // }

  // toggleRequestSelection(request: { id: number; formName: string; status: string }): void {
  //   const index = this.selectedRequests.findIndex(r => r.id === request.id);
  //   if (index === -1) {
  //     this.selectedRequests.push(request);
  //   } else {
  //     this.selectedRequests.splice(index, 1);
  //   }
  // }

  // submitRequests(): void {
  //   this.selectedRequests.forEach(request => {
  //     request.status = 'Sent';
  //     this.submittedRequests.push(request);
  //   });
  // }
  //   localStorage.setItem(`submitted_requests_${this.currentUserId}`, JSON.stringify(this.submittedRequests));
  //   this.requests = this.requests.filter(request => !this.selectedRequests.includes(request));
  //   this.selectedRequests = [];
  //   localStorage.setItem(`requests_${this.currentUserId}`, JSON.stringify(this.requests));
  //   alert('The selected requests have been submitted!');
  // }
}

