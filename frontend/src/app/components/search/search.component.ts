import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  imports: [CommonModule, FormsModule] 
})
export class SearchComponent {
  @Output() filtersApplied = new EventEmitter<any>();

  showFilters: boolean = false;
  searchId: string = '';
  filterFormType: string = '';
  filterStatus: string = '';

  formTypes: string[] = ['Form1', 'Form2', 'Form3']; 
  statuses: string[] = ['Sent', 'Approved', 'Rejected']; 

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  applyFilters(): void {
    const filters = {
      id: this.searchId,
      formType: this.filterFormType,
      status: this.filterStatus
    };
    console.log('Filters applied:', filters); // Debugging
    this.filtersApplied.emit(filters);
    }

}