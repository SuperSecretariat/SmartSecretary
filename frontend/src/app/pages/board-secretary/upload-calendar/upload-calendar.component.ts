import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface FileItem {
  name: string;
  type: 'Folder' | 'File';
  size?: string;
}

@Component({
  selector: 'app-upload-calendar',
  standalone: false,
  templateUrl: './upload-calendar.component.html',
  styleUrl: './upload-calendar.component.css'
})

export class UploadCalendarComponent {
  searchQuery = '';
  selectedFile: File | null = null;

  constructor(private http: HttpClient) { }

  items: FileItem[] = [ ];

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

      this.http.post('http://localhost:8081/api/calendar/add', formData, {
        headers: {Authorization: 'Bearer ' + token},
      }).subscribe({
        next: () => {
          this.items.push({
            name: this.selectedFile!.name,
            type: 'File',
            size: (this.selectedFile!.size  / 1024).toFixed(2) + ' KB'
          });
          this.selectedFile = null; // Reset selected file after upload
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

  reloadDocs() {
    console.log('Reload Calendar Documentation triggered');
    // Implement actual logic here if needed
  }
}
