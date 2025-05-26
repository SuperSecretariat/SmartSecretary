import { Component, OnInit } from '@angular/core';
import { FormsService } from '../../../components/_services/forms.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-board-secretary-view-tickets',
  standalone: false,
  templateUrl: './board-secretary-view-tickets.component.html',
  styleUrl: './board-secretary-view-tickets.component.css'
})
export class BoardSecretaryViewTicketsComponent {
  submittedRequests: {id: number; formTitle: string; status: string }[] = [];
  filteredRequests: {id: number; formTitle: string; status: string }[] = [];
  searchTerm: string = '';

  constructor(
    private formsService: FormsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSubmittedRequests();
  }

  private loadSubmittedRequests(): void {
    this.formsService.getAllSubmittedRequests().subscribe({
      next: data => {
        this.submittedRequests = data;
        this.filteredRequests = [...this.submittedRequests];
      },
      error: error => {
        console.error('Error fetching submitted requests:', error);
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      this.filteredRequests = [...this.submittedRequests];
      return;
    }
    this.filteredRequests = this.submittedRequests.filter(request =>
      request.id.toString().includes(term) ||
      request.formTitle.toLowerCase().includes(term)
    );
  }

  viewForm(id: number) {
    this.router.navigate([`secretary/dashboard/view-form/`, id])
  }
}
