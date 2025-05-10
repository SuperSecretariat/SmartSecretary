import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-submitted-forms',
  templateUrl: './submitted-forms.component.html',
  styleUrls: ['./submitted-forms.component.css'],
  imports: [CommonModule, FormsModule, SearchComponent]
})
export class SubmittedFormsComponent implements OnInit {
  submittedRequests: { id: number; formName: string; status: string }[] = [];
  filteredRequests: { id: number; formName: string; status: string }[] = [];
  currentUserId: string = 'user123'; 

  ngOnInit(): void {
    this.loadSubmittedRequests();
  }

  private loadSubmittedRequests(): void {
    const storedSubmittedRequests = localStorage.getItem(`submitted_requests_${this.currentUserId}`);
    if (storedSubmittedRequests) {
      this.submittedRequests = JSON.parse(storedSubmittedRequests);
    } else {
      this.submittedRequests = [];
    }
    this.filteredRequests = [...this.submittedRequests];
  }

  onFiltersApplied(filters: any): void {
    console.log('Filters applied:', filters);

    this.filteredRequests = this.submittedRequests.filter(request => {
      const matchesId = filters.id ? request.id.toString().includes(filters.id) : true;
      const matchesFormType = filters.formType ? request.formName === filters.formType : true;
      const matchesStatus = filters.status ? request.status === filters.status : true;

      return matchesId && matchesFormType && matchesStatus;
    });
  }
}