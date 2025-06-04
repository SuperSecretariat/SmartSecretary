import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface FileItem {
  name: string;
  type: 'Folder' | 'File';
  size?: string;
  groupsPopulated?: string[];
}
interface UploadResponse {
  message: string;
  groups: string[];
}

@Component({
  selector: 'app-upload-calendar',
  standalone: false,
  templateUrl: './upload-calendar.component.html',
  styleUrl: './upload-calendar.component.css'
})

export class UploadCalendarComponent implements OnInit {
  searchQuery = '';
  deleteGroupQuery = '';
  selectedFile: File | null = null;
  errorMessage: string | null = null;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadUploadedFiles();
    this.loadUploadedExamFiles();
    this.loadUploadedYearlyFiles();
  }

  items: FileItem[] = [ ];
  showExams: boolean = false;
  annualView: boolean = false;

  filteredItems(): FileItem[] {
    const query = this.searchQuery.toLowerCase();
    return this.items.filter(item => item.name.toLowerCase().includes(query));
  }

  deleteItem(item: FileItem) {
    this.items = this.items.filter(i => i !== item);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
        this.selectedFile = file;
      } else {
        alert('Please select a CSV file');
        input.value = '';
      }
    }
  }

  uploadFile() {
    if (this.selectedFile) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.http.post<UploadResponse>('http://localhost:8081/api/calendar/add', formData, {
        headers: {Authorization: 'Bearer ' + token},
      }).subscribe({
        next: (response) => {
          this.items.push({
            name: this.selectedFile!.name,
            type: 'File',
            size: (this.selectedFile!.size  / 1024).toFixed(2) + ' KB',
            groupsPopulated: response.groups
          });
          this.selectedFile = null; // Reset selected file after upload
          this.errorMessage = response.message; // Clear any previous error message
          const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
          if (fileInput) {
            fileInput.value = ''; // Clear the file input
          }
          console.log('File uploaded successfully');
        },
        error: (error) => {
          console.error('Error uploading file:', error);
        }
      });
    }
  }

  loadUploadedFiles() {
    this.http.get<any[]>('http://localhost:8081/api/calendar/files').subscribe({
      next: (files) => {
        this.items = files.map(file => ({
          name: file.name,
          type: 'File',
          size: file.size.toFixed(2) + ' KB',
          groupsPopulated: file.groups.split(',').map((g: string) => g.trim())
        }));
      },
      error: (err) => {
        console.error('Error loading uploaded files:', err);
      }
    });
  }

  deleteByGroup() {
    const trimmedGroup = this.deleteGroupQuery.trim();
    if (!trimmedGroup) {
      alert('Please enter a group name to delete');
      return;
    }

    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

    const confirmDelete = confirm(`Are you sure you want to delete all events for group "${trimmedGroup}"?`);

    if (confirmDelete) {
      this.http.delete(`http://localhost:8081/api/calendar/delete-group/${encodeURIComponent(trimmedGroup)}`, {
        headers: { Authorization: 'Bearer ' + token }
      }).subscribe({
        next: (response) => {
          console.log(`Deleted events for group: ${trimmedGroup}`);
          this.errorMessage = null;
          this.deleteGroupQuery = ''; // Clear the input
          this.loadUploadedFiles();   // Refresh UI
        },
        error: (error) => {
          console.error('Error deleting group events:', error);
          this.errorMessage = error.error?.message || 'Error deleting group events. Please try again.';
        }
      });
    }
  }

  deleteAllCalendarDocumentation() {
    const confirmDelete = confirm('Are you sure you want to delete ALL calendar documentation? This action cannot be undone.');

    if (confirmDelete) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      this.http.delete('http://localhost:8081/api/calendar/delete-all', {
        headers: {Authorization: 'Bearer ' + token}
      }).subscribe({
        next: (response) => {
          console.log('Deleted all calendar documentation');
          this.errorMessage = null;
          this.items = []; // Clear the local items array
        },
        error: (error) => {
          console.error('Error deleting all calendar documentation:', error);
          this.errorMessage = error.error?.message || 'Error deleting all calendar documentation. Please try again.';
        }
      });
    }
  }

  toggleExamMode() {
    this.showExams=!this.showExams;
  }

  toggleYearlyMode() {
    this.annualView=!this.annualView;
  }

  // ---------------------------------------------------------------------------------------------------- //
  // ------------------------------------- Yearly Calendar Functions ------------------------------------ //
  // ---------------------------------------------------------------------------------------------------- //

  deleteYearlyGroupQuery: string = '';
  yearlySearchQuery: string = '';
  selectedYearlyFile: File | null = null;
  yearlyItems: FileItem[] = [];

  filteredYearlyItems(): FileItem[] {
    const query = this.yearlySearchQuery.toLowerCase();
    return this.yearlyItems.filter(item => item.name.toLowerCase().includes(query));
  }

  deleteAllYearlyDocumentation() {
    const confirmDelete = confirm('Are you sure you want to delete ALL yearly calendar documentation? This action cannot be undone.');

    if (confirmDelete) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      this.http.delete('http://localhost:8081/api/calendar/yearly/delete-all', {
        headers: {Authorization: 'Bearer ' + token}
      }).subscribe({
        next: (response) => {
          console.log('Deleted all yearly calendar documentation');
          this.errorMessage = null;
          this.yearlyItems = []; // Clear the local items array
        },
        error: (error) => {
          console.error('Error deleting all yearly calendar documentation:', error);
          this.errorMessage = error.error?.message || 'Error deleting all yearly calendar documentation. Please try again.';
        }
      });
    }
  }

  uploadYearlyFile() {
    if (this.selectedYearlyFile) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      const formData = new FormData();
      formData.append('file', this.selectedYearlyFile);

      this.http.post<UploadResponse>('http://localhost:8081/api/calendar/yearly/add', formData, {
        headers: {Authorization: 'Bearer ' + token},
      }).subscribe({
        next: (response) => {
          this.yearlyItems.push({
            name: this.selectedYearlyFile!.name,
            type: 'File',
            size: (this.selectedYearlyFile!.size  / 1024).toFixed(2) + ' KB'
          });
          this.selectedYearlyFile = null;
          this.errorMessage = response.message;
          // Clear the yearly file input
          const fileInputs = document.querySelectorAll('input[type="file"]');
          fileInputs.forEach((input: any) => {
            if (input.getAttribute('ng-reflect-model') === 'yearlyFile') {
              input.value = '';
            }
          });
          console.log('Yearly file uploaded successfully');
        },
        error: (error) => {
          console.error('Error uploading yearly file:', error);
        }
      });
    }
  }

  onYearlyFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
        this.selectedYearlyFile = file;
      } else {
        alert('Please select a CSV file');
        input.value = '';
      }
    }
  }

  loadUploadedYearlyFiles() {
    this.http.get<any[]>('http://localhost:8081/api/calendar/yearly/files').subscribe({
      next: (files) => {
        this.yearlyItems = files.map(file => ({
          name: file.name,
          type: 'File',
          size: file.size.toFixed(2) + ' KB',
          groupsPopulated: file.groups.split(',').map((g: string) => g.trim())
        }));
      },
      error: (err) => {
        console.error('Error loading uploaded yearly files:', err);
      }
    });
  }

  // ---------------------------------------------------------------------------------------------------- //
  // --------------------------------------- Exam Upload Functions -------------------------------------- //
  // ---------------------------------------------------------------------------------------------------- //
  deleteExamGroupQuery: string = '';
  examSearchQuery: string = '';
  selectedExamFile: File | null = null;
  examItems: FileItem[] = [];

  filteredExamItems(): FileItem[] {
    const query = this.examSearchQuery.toLowerCase();
    return this.examItems.filter(item => item.name.toLowerCase().includes(query));
  }

  deleteExamByGroup() {
    const trimmedGroup = this.deleteExamGroupQuery.trim();
    if (!trimmedGroup) {
      alert('Please enter a group name to delete');
      return;
    }

    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

    const confirmDelete = confirm(`Are you sure you want to delete all exams for group "${trimmedGroup}"?`);

    if (confirmDelete) {
      this.http.delete(`http://localhost:8081/api/calendar/exam/delete-group/${encodeURIComponent(trimmedGroup)}`, {
        headers: { Authorization: 'Bearer ' + token }
      }).subscribe({
        next: (response) => {
          console.log(`Deleted exams for group: ${trimmedGroup}`);
          this.errorMessage = null;
          this.deleteExamGroupQuery = ''; // Clear the input
          this.loadUploadedExamFiles();   // Refresh UI
        },
        error: (error) => {
          console.error('Error deleting group exams:', error);
          this.errorMessage = error.error?.message || 'Error deleting group exams. Please try again.';
        }
      });
    }
  }

  deleteAllExamDocumentation() {
    const confirmDelete = confirm('Are you sure you want to delete ALL exam documentation? This action cannot be undone.');

    if (confirmDelete) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      this.http.delete('http://localhost:8081/api/calendar/exam/delete-all', {
        headers: {Authorization: 'Bearer ' + token}
      }).subscribe({
        next: (response) => {
          console.log('Deleted all exam documentation');
          this.errorMessage = null;
          this.examItems = []; // Clear the local items array
        },
        error: (error) => {
          console.error('Error deleting all exam documentation:', error);
          this.errorMessage = error.error?.message || 'Error deleting all exam documentation. Please try again.';
        }
      });
    }
  }

  uploadExamFile() {
    if (this.selectedExamFile) {
      const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';

      const formData = new FormData();
      formData.append('file', this.selectedExamFile);

      this.http.post<UploadResponse>('http://localhost:8081/api/calendar/add-exam', formData, {
        headers: {Authorization: 'Bearer ' + token},
      }).subscribe({
        next: (response) => {
          this.examItems.push({
            name: this.selectedExamFile!.name,
            type: 'File',
            size: (this.selectedExamFile!.size  / 1024).toFixed(2) + ' KB',
            groupsPopulated: response.groups
          });
          this.selectedExamFile = null;
          this.errorMessage = response.message;
          // Clear the exam file input
          const fileInputs = document.querySelectorAll('input[type="file"]');
          fileInputs.forEach((input: any) => {
            if (input.getAttribute('ng-reflect-model') === 'examFile') {
              input.value = '';
            }
          });
          console.log('Exam file uploaded successfully');
        },
        error: (error) => {
          console.error('Error uploading exam file:', error);
        }
      });
    }
  }

  onExamFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
        this.selectedExamFile = file;
      } else {
        alert('Please select a CSV file');
        input.value = '';
      }
    }
  }

  loadUploadedExamFiles() {
    this.http.get<any[]>('http://localhost:8081/api/calendar/exam/files').subscribe({
      next: (files) => {
        this.examItems = files.map(file => ({
          name: file.name,
          type: 'File',
          size: file.size.toFixed(2) + ' KB',
          groupsPopulated: file.groups.split(',').map((g: string) => g.trim())
        }));
      },
      error: (err) => {
        console.error('Error loading uploaded exam files:', err);
      }
    });
  }
}
