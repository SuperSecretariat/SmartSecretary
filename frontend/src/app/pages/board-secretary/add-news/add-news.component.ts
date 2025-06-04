import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NewsService } from '../../../components/_services/news.service';

@Component({
  selector: 'app-add-news',
  templateUrl: './add-news.component.html',
  standalone: false,
  styleUrls: ['./add-news.component.css']
})
export class AddNewsComponent {
  @Input() newsToEdit: any = null; // If set, edit mode. Otherwise, add mode.
  @Output() success = new EventEmitter<void>();

  news: {
    title: string;
    body: string;
    hidden: boolean;
    fileName?: string;
    fileType?: string;
    fileData?: any;
  } = {
    title: '',
    body: '',
    hidden: false
  };

  file: File | null = null;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private newsService: NewsService) {}

  ngOnChanges() {
    if (this.newsToEdit) {
      this.news = {
        title: this.newsToEdit.title,
        body: this.newsToEdit.body,
        hidden: this.newsToEdit.hidden,
        fileName: this.newsToEdit.fileName,
        fileType: this.newsToEdit.fileType,
        fileData: this.newsToEdit.fileData
      };
      this.file = null;
      this.successMessage = '';
      this.errorMessage = '';
    } else {
      this.news = { title: '', body: '', hidden: false };
      this.file = null;
      this.successMessage = '';
      this.errorMessage = '';
    }
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) this.file = file;
  }

  onSubmit() {
    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';
    const obs = this.newsToEdit
      ? this.newsService.updateNewsWithFile(this.newsToEdit.id, this.news, this.file)
      : this.newsService.addNews(this.news, this.file!);

    obs.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = this.newsToEdit
          ? 'News post updated successfully!'
          : 'News post added successfully!';
        this.news = { title: '', body: '', hidden: false };
        this.file = null;
        this.success.emit();
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = this.newsToEdit
          ? 'Failed to update news post.'
          : 'Failed to add news post.';
      }
    });
  }
}
