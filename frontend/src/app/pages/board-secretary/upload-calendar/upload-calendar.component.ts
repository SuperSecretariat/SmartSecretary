import { Component } from '@angular/core';

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
      this.selectedFile = input.files[0];
    }
  }

  uploadFile() {
    if (this.selectedFile) {
      this.items.push({
        name: this.selectedFile.name,
        type: 'File',
        size: `${(this.selectedFile.size / 1024).toFixed(1)} KB`
      });
      this.selectedFile = null;

      const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = '';
      }
    }
  }

  reloadDocs() {
    console.log('Reload Calendar Documentation triggered');
    // Implement actual logic here if needed
  }
}
