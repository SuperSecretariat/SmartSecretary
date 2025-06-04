import { Component, OnInit } from '@angular/core';
import { FormFilesService, FileEntry } from '../../../components/_services/form-files.service';
import { FormsService } from '../../../components/_services/forms.service';

@Component({
  selector: 'app-board-secretary-change-forms',
  standalone: false,
  templateUrl: './board-secretary-change-forms.component.html',
  styleUrl: './board-secretary-change-forms.component.css'
})
export class BoardSecretaryChangeFormsComponent {
  pathSegments: string[] = [];
  entries: FileEntry[] = [];
  selectedFile: File | null = null;
  newFolderName: string = '';
  searchTerm: string = '';
  errorMessage: string = '';

  constructor(
    private fileManager: FormFilesService,
    private formsService: FormsService
  ) { }

  ngOnInit() {
    this.loadEntries();
  }

  get currentDir(): string {
    return this.pathSegments.join('/');
  }

  loadEntries() {
    this.fileManager.listFiles(this.currentDir).subscribe(entries => this.entries = entries);
  }

  navigateTo(segmentIdx: number) {
    this.pathSegments = this.pathSegments.slice(0, segmentIdx + 1);
    this.loadEntries();
  }

  enterDirectory(name: string) {
    this.pathSegments.push(name);
    this.loadEntries();
  }

  goUp() {
    if (this.pathSegments.length > 0) {
      this.pathSegments.pop();
      this.loadEntries();
    }
  }

  selectFile(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;
  }

  upload() {
    if (!this.selectedFile) {
      this.errorMessage = "No file selected.";
      return;
    }

    const fileName = this.selectedFile.name;
    if (!fileName.toLowerCase().endsWith('.docx')) {
      this.errorMessage = "Only .docx files are allowed.";
      return;
    }

    this.errorMessage = ""; // Clear any previous error
    this.fileManager.uploadDocument(this.selectedFile, this.currentDir).subscribe(() => {
      this.selectedFile = null;
      this.loadEntries();

      const formTitle = fileName.replace('.docx', '');
      const isActive = true;
      this.formsService.createForm(formTitle, isActive).subscribe({
        next: (response) => {
          console.log('Form created:', response);
        },
        error: (err) => {
          this.errorMessage = err.error || "Form creation failed";
        }
      });

    }, err => {
      this.errorMessage = err.error || "Upload failed";
    });
  }

  download(entry: FileEntry) {
    this.fileManager.downloadFile(this.currentDir, entry.name).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = entry.name;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  delete(entry: FileEntry) {
    if (confirm(`Are you sure you want to delete ${entry.name}?`)) {
      this.fileManager.deleteEntry(this.currentDir, entry.name).subscribe(() => {
        this.loadEntries()
        console.log(entry.name);
        this.formsService.deleteFormByTitle(entry.name).subscribe({
          next: (response) => {
            console.log('Form deleted:', response);
          },
          error: (err) => {
            this.errorMessage = err.error || "Form deletion failed";
          }
        });
      });
    }
  }

  createDirectory() {
    if (this.newFolderName.trim()) {
      this.fileManager.createDirectory(this.currentDir, this.newFolderName.trim()).subscribe(() => {
        this.newFolderName = '';
        this.loadEntries();
      });
    }
  }

  filteredEntries(): FileEntry[] {
    if (!this.searchTerm.trim()) {
      return this.entries;
    }
    const lower = this.searchTerm.toLowerCase();
    return this.entries.filter(entry => entry.name.toLowerCase().includes(lower));
  }
}

