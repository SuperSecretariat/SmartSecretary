import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SearchComponent } from '../search/search.component';
import { FormsService } from '../_services/forms.service';

@Component({
  selector: 'app-submitted-forms',
  templateUrl: './submitted-forms.component.html',
  styleUrls: ['./submitted-forms.component.css'],
  imports: [CommonModule, FormsModule, SearchComponent]
})
export class SubmittedFormsComponent implements OnInit {
  submittedRequests: { id: number; formTitle: string; status: string }[] = [];
  filteredRequests: { id: number; formTitle: string; status: string }[] = [];

  constructor(private readonly formsService: FormsService) {}

  ngOnInit(): void {
    this.loadSubmittedRequests();
  }

  private loadSubmittedRequests(): void {
    // const storedSubmittedRequests = localStorage.getItem(`submitted_requests_${this.currentUserId}`);
    // if (storedSubmittedRequests) {
    //   this.submittedRequests = JSON.parse(storedSubmittedRequests);
    // } else {
    //   this.submittedRequests = [];
    // }
    // this.filteredRequests = [...this.submittedRequests];
    this.formsService.getSubmittedRequests().subscribe({
      next: data => {
        this.submittedRequests = data;
        this.filteredRequests = [...this.submittedRequests];
      },
      error: error => {
        console.error('Error fetching submitted requests:', error);
      }
    });
  }

  onFiltersApplied(filters: any): void {
    console.log('Filters applied:', filters);

    this.filteredRequests = this.submittedRequests.filter(request => {
      const matchesId = filters.id ? request.id.toString().includes(filters.id) : true;
      const matchesFormType = filters.formType ? request.formTitle === filters.formType : true;
      const matchesStatus = filters.status ? request.status === filters.status : true;

      return matchesId && matchesFormType && matchesStatus;
    });
  }
}