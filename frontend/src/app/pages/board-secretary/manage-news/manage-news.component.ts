import { Component, OnInit } from '@angular/core';
import { NewsService } from '../../../components/_services/news.service';

@Component({
  selector: 'app-manage-news',
  templateUrl: './manage-news.component.html',
  styleUrls: ['./manage-news.component.css'],
  standalone: false
})
export class ManageNewsComponent implements OnInit {
  allNews: any[] = [];     // Store all news here
  newsList: any[] = [];    // Filtered list to display
  searchTitle: string = '';
  hiddenFilter: 'all' | 'hidden' | 'visible' = 'all';
  showEditForm = false;
  showAddForm = false;
  selectedNews: any = null;

  constructor(private newsService: NewsService) {}

  ngOnInit() {
    this.fetchNews();
  }

  fetchNews() {
    this.newsService.getNews().subscribe(data => {
      this.allNews = data;
      this.applyFilters();
    });
  }

  applyFilters() {
    let filtered = this.allNews;

    // Filter by search title
    if (this.searchTitle.trim()) {
      filtered = filtered.filter(news =>
        news.title.toLowerCase().includes(this.searchTitle.trim().toLowerCase())
      );
    }

    // Filter by hidden state
    if (this.hiddenFilter === 'hidden') {
      filtered = filtered.filter(news => news.hidden);
    } else if (this.hiddenFilter === 'visible') {
      filtered = filtered.filter(news => !news.hidden);
    }

    this.newsList = filtered;
  }

  onSearch() {
    this.applyFilters();
  }

  onFilterChange(filter: 'all' | 'hidden' | 'visible') {
    this.hiddenFilter = filter;
    this.applyFilters();
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
    this.showEditForm = false;
    this.selectedNews = null;
  }

  onHideNews(news: any) {
    this.newsService.updateNewsWithFile(news.id, { ...news, hidden: !news.hidden }).subscribe(() => this.fetchNews());
  }

  onEditNews(news: any) {
    this.selectedNews = news;
    this.showAddForm = false;
    this.showEditForm = !this.showEditForm;
  }

  onDeleteNews(news: any) {
    if (confirm('Are you sure you want to delete this news post?')) {
      this.newsService.deleteNews(news.id).subscribe(() => this.fetchNews());
    }
  }

  onAddEditSuccess() {
    this.showAddForm = false;
    this.showEditForm = false;
    this.selectedNews = null;
    this.fetchNews();
  }
}
