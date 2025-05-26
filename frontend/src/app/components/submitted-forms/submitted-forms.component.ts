import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormsService } from '../_services/forms.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-submitted-forms',
  templateUrl: './submitted-forms.component.html',
  styleUrls: ['./submitted-forms.component.css'],
  imports: [CommonModule, FormsModule]
})
export class SubmittedFormsComponent implements OnInit {
  submittedRequests: { id: number; formTitle: string; status: string }[] = [];
  filteredRequests: { id: number; formTitle: string; status: string }[] = [];
  searchTerm: string = '';

  constructor(private readonly formsService: FormsService, private readonly router: Router) {}

  ngOnInit(): void {
    this.loadSubmittedRequests();
  }

  private loadSubmittedRequests(): void {
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
    this.router.navigate([`student/view-form`, id])
  }
}