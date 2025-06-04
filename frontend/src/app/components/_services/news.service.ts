import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environments';

const NEWS_API = `${environment.backendUrl}/api/news`;
@Injectable({
  providedIn: 'root'
})
export class NewsService {

  constructor(private http: HttpClient) {}

  getNews() {
    return this.http.get<any[]>(NEWS_API);
  }

  downloadFile(newsId: number) {
    return this.http.get(
      NEWS_API+`/${newsId}/file`,
      {
        responseType: 'blob'
      });
  }

  deleteNews(id: number) {
    return this.http.delete(
      NEWS_API+`/${id}`
    );
  }

  updateNewsWithFile(
    id: number,
    news: {
      title: string;
      body: string;
      hidden: boolean;
      fileName?: string;
      fileType?: string;
      fileData?: any;
    },
    file?: File | null
  ) {
    const formData = new FormData();
    formData.append('title', news.title ?? '');
    formData.append('body', news.body ?? '');
    formData.append('hidden', String(news.hidden === undefined ? false : news.hidden));
    if (file) {
      formData.append('file', file, file.name);
    }
    return this.http.put<any>(
      NEWS_API+`/${id}`,
      formData
    );
  }

  addNews(
    news: {
      title: string;
      body: string;
      hidden: boolean;
      fileName?: string;
      fileType?: string;
      fileData?: any;
    },
    file?: File
  ): Observable<any> {
    const formData = new FormData();
    formData.append('title', news.title ?? '');
    formData.append('body', news.body ?? '');
    formData.append('hidden', String(news.hidden));

    if (file) {
      formData.append('file', file, file.name);
    }

    return this.http.post<any>(
      NEWS_API,
      formData
    );
  }
}
