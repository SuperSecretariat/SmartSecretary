import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface ExamFileItem {
  name: string;
  type: 'Folder' | 'File';
  size?: string;
  groupsPopulated?: string[];
  examDate?: string;
  examType?: string;
}

interface ExamUploadResponse {
  message: string;
  groups: string[];
  examDate?: string;
  examType?: string;
}

@Component({
  selector: 'app-upload-exam-calendar',
  templateUrl: './upload-exam-calendar.component.html',
  styleUrl: './upload-exam-calendar.component.css',
  standalone: false
})
export class UploadExamCalendarComponent implements OnInit {
  @Output() switchBack = new EventEmitter<void>();

  searchQuery = '';
  deleteGroupQuery = '';
  deleteExamDateQuery = '';
  selectedFile: File | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUploadedExamFiles();
  }

  items: ExamFileItem[] = [];

  filteredItems(): ExamFileItem[] {
    const query = this.searchQuery.toLowerCase();
    return this.items.filter(item =>
      item.name.toLowerCase().includes(query) ||
      (item.groupsPopulated && item.groupsPopulated.some(group =>
        group.toLowerCase().includes(query)
      )) ||
      (item.examDate && item.examDate.toLowerCase().includes(query))
    );
  }

  deleteItem(item: ExamFileItem) {
    this.items = this.items.filter(i => i !== item);
  }

  backToCalendar() {
    this.switchBack.emit();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
        this.selectedFile = file;
        this.errorMessage = null;
      } else {
        this.errorMessage = 'Please select a CSV file';
        this.selectedFile = null;
        input.value = '';
      }
    }
  }

  uploadExamFile() {
    if (this.selectedFile) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.http.post<ExamUploadResponse>('http://localhost:8081/api/calendar/add-exam', formData, {
        headers: {Authorization: 'Bearer ' + token},
      }).subscribe({
        next: (response) => {
          this.items.push({
            name: this.selectedFile!.name,
            type: 'File',
            size: (this.selectedFile!.size / 1024).toFixed(2) + ' KB',
            groupsPopulated: response.groups,
            examDate: response.examDate,
            examType: response.examType
          });
          this.selectedFile = null;
          this.successMessage = response.message;
          this.errorMessage = null;

          const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
          if (fileInput) {
            fileInput.value = '';
          }

          setTimeout(() => {
            this.successMessage = null;
          }, 5000);
        },
        error: (error) => {
          console.error('Error uploading exam file:', error);
          this.errorMessage = error.error?.message || 'Error uploading exam file. Please try again.';
          this.successMessage = null;
        }
      });
    } else {
      this.errorMessage = 'Please select a file first.';
    }
  }

  loadUploadedExamFiles() {
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

    this.http.get<any[]>('http://localhost:8081/api/calendar/exam-files', {
      headers: {Authorization: 'Bearer ' + token}
    }).subscribe({
      next: (files) => {
        this.items = files.map(file => ({
          name: file.name,
          type: 'File',
          size: file.size.toFixed(2) + ' KB',
          groupsPopulated: file.groups ? file.groups.split(',').map((g: string) => g.trim()) : [],
          examDate: file.examDate,
          examType: file.examType
        }));
      },
      error: (err) => {
        console.error('Error loading uploaded exam files:', err);
        this.errorMessage = 'Error loading exam files. Please refresh the page.';
      }
    });
  }

  deleteByGroup() {
    const trimmedGroup = this.deleteGroupQuery.trim();
    if (!trimmedGroup) {
      this.errorMessage = 'Please enter a group name to delete';
      return;
    }

    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

    const confirmDelete = confirm(`Are you sure you want to delete all exams for group "${trimmedGroup}"?`);

    if (confirmDelete) {
      this.http.delete(`http://localhost:8081/api/calendar/delete-exam-group/${encodeURIComponent(trimmedGroup)}`, {
        headers: { Authorization: 'Bearer ' + token }
      }).subscribe({
        next: () => {
          this.successMessage = `Successfully deleted all exams for group "${trimmedGroup}"`;
          this.errorMessage = null;
          this.deleteGroupQuery = '';
          this.loadUploadedExamFiles();

          setTimeout(() => {
            this.successMessage = null;
          }, 5000);
        },
        error: (error) => {
          console.error('Error deleting group exams:', error);
          this.errorMessage = error.error?.message || 'Error deleting group exams. Please try again.';
        }
      });
    }
  }

  deleteByExamDate() {
    const trimmedDate = this.deleteExamDateQuery.trim();
    if (!trimmedDate) {
      this.errorMessage = 'Please enter an exam date to delete';
      return;
    }

    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

    const confirmDelete = confirm(`Are you sure you want to delete all exams scheduled for "${trimmedDate}"?`);

    if (confirmDelete) {
      this.http.delete(`http://localhost:8081/api/calendar/delete-exam-date/${encodeURIComponent(trimmedDate)}`, {
        headers: { Authorization: 'Bearer ' + token }
      }).subscribe({
        next: () => {
          this.successMessage = `Successfully deleted all exams for date "${trimmedDate}"`;
          this.errorMessage = null;
          this.deleteExamDateQuery = '';
          this.loadUploadedExamFiles();

          setTimeout(() => {
            this.successMessage = null;
          }, 5000);
        },
        error: (error) => {
          console.error('Error deleting exams by date:', error);
          this.errorMessage = error.error?.message || 'Error deleting exams by date. Please try again.';
        }
      });
    }
  }

  deleteAllExamDocumentation() {
    const confirmDelete = confirm('Are you sure you want to delete ALL exam documentation? This action cannot be undone.');

    if (confirmDelete) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      this.http.delete('http://localhost:8081/api/calendar/delete-all-exams', {
        headers: {Authorization: 'Bearer ' + token}
      }).subscribe({
        next: () => {
          this.successMessage = 'Successfully deleted all exam documentation';
          this.errorMessage = null;
          this.items = [];

          setTimeout(() => {
            this.successMessage = null;
          }, 5000);
        },
        error: (error) => {
          console.error('Error deleting all exam documentation:', error);
          this.errorMessage = error.error?.message || 'Error deleting all exam documentation. Please try again.';
        }
      });
    }
  }
}
