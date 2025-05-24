import { Component, OnInit } from '@angular/core';
import { LlmFilesService, FileEntry } from '../../../components/_services/llm-files.service';
@Component({
  selector: 'app-board-admin-llm-files',
  standalone: false,
  templateUrl: './board-admin-llm-files.component.html',
  styleUrl: './board-admin-llm-files.component.css'
})
export class BoardAdminLlmFilesComponent {
  pathSegments: string[] = [];
  entries: FileEntry[] = [];
  selectedFile: File | null = null;
  newFolderName: string = '';
  searchTerm: string = '';

  constructor(private fileManager: LlmFilesService) {}

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

  reload(){
    this.fileManager.reloadDocumentation().subscribe(() => {

    });
  }

  selectFile(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;
  }

  upload() {
    if (this.selectedFile) {
      this.fileManager.uploadFile(this.selectedFile, this.currentDir).subscribe(() => {
        this.selectedFile = null;
        this.loadEntries();
      });
    }
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
      this.fileManager.deleteEntry(this.currentDir, entry.name).subscribe(() => this.loadEntries());
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

