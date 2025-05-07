import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-submitted-forms',
  templateUrl: './submitted-forms.component.html',
  styleUrls: ['./submitted-forms.component.css'],
  imports: [CommonModule, FormsModule]
})
export class SubmittedFormsComponent {
  submittedRequests: { id: number; formName: string; status: string }[] = [];
  filteredRequests: { id: number; formName: string; status: string }[] = [];
  currentUserId: string = 'user123';
  searchId: string = '';
  filterFormName: string = '';
  filterStatus: string = '';
  formTypes: string[] = ['Form1', 'Form2', 'Form3'];
  statuses: string[] = ['Pending', 'Accepted', 'Rejected'];
  showSearchBar: boolean = false;

  constructor() {
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

  applyFilters(): void {
    this.filteredRequests = this.submittedRequests.filter(request => {
      const matchesId = this.searchId ? request.id.toString().includes(this.searchId) : true;
      const matchesFormName = this.filterFormName ? request.formName === this.filterFormName : true;
      const matchesStatus = this.filterStatus ? request.status === this.filterStatus : true;
      return matchesId && matchesFormName && matchesStatus;
    });
  }

  toggleSearchBar(): void {
    this.showSearchBar = !this.showSearchBar;
  }
}