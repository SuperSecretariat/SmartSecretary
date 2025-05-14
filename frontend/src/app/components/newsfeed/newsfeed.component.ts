import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SearchComponent } from '../search/search.component';
import { NewsItemComponent } from '../news-item/news-item.component';

@Component({
  selector: 'app-newsfeed',
  imports: [CommonModule, FormsModule, SearchComponent, NewsItemComponent],
  templateUrl: './newsfeed.component.html',
  styleUrls: ['./newsfeed.component.css']
})
export class NewsfeedComponent implements OnInit {
  news: { id: number; title: string; status: string }[] = [];
  filteredNews: { id: number; title: string; status: string }[] = [];

  ngOnInit(): void {
    this.loadNews();
  }

  private loadNews(): void {
    const storedNews = localStorage.getItem('news_feed_data');
    if (storedNews) {
      this.news = JSON.parse(storedNews);
    } else {
      this.news = [];
    }
    this.filteredNews = [...this.news];
  }

  onFiltersApplied(filters: any): void {
    this.filteredNews = this.news.filter(item => {
      const matchesId = filters.id ? item.id.toString().includes(filters.id) : true;
      const matchesFormType = filters.formType ? item.title === filters.formType : true;
      const matchesStatus = filters.status ? item.status === filters.status : true;
      return matchesId && matchesFormType && matchesStatus;
    });
  }

  getMessage(status: string): string {
    switch (status.toLowerCase()) {
      case 'approved':
        return 'Your request has been approved by the secretary.';
      case 'rejected':
        return 'Unfortunately, your request was rejected.';
      case 'sent':
        return 'Your request was submitted and is pending review.';
      default:
        return 'Status unknown.';
    }
  }
  
}
