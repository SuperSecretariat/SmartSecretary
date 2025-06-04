import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NewsService } from '@services/news.service';

@Component({
  selector: 'app-newsfeed',
  imports: [CommonModule, FormsModule],
  templateUrl: './newsfeed.component.html',
  styleUrls: ['./newsfeed.component.css']
})
export class NewsfeedComponent implements OnInit {
  newsList: any[] = [];
  loading: boolean = true;

  constructor(private newsService: NewsService) {}

  ngOnInit() {
    this.fetchNews();
  }

  fetchNews() {
    this.loading = true;
    this.newsService.getNews().subscribe({
      next: data => {
        // Only show visible news and order by creation date descending (newest first)
        this.newsList = (data || [])
          .filter(n => !n.hidden)
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.loading = false;
      },
      error: _ => { this.newsList = []; this.loading = false; }
    });
  }

  downloadFile(news: any) {
    if (!news.fileName) return;
    this.newsService.downloadFile(news.id).subscribe(blob => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = news.fileName;
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }
}
