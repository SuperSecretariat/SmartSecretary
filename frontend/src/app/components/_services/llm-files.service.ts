import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

const LLMFILES_API = 'http://localhost:8081/api/llmFiles';

export interface FileEntry {
  name: string;
  isDirectory: boolean;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class LlmFilesService {

  constructor(private http: HttpClient) {}

  listFiles(dir: string = ''): Observable<FileEntry[]> {
    return this.http.get<FileEntry[]>(
      LLMFILES_API,
      {
        params: { dir }
      });
  }

  uploadFile(file: File, dir: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('dir', dir);
    return this.http.post(
      LLMFILES_API,
      formData
    );
  }

  downloadFile(dir: string, name: string): Observable<Blob> {
    const params = new HttpParams().set('dir', dir).set('file', name);
    return this.http.get(
      LLMFILES_API + '/download',
      {
        params, responseType: 'blob'
      },
      );
  }

  deleteEntry(dir: string, name: string): Observable<any> {
    const params = new HttpParams().set('dir', dir).set('name', name);
    return this.http.delete(
      LLMFILES_API,
      {
        params
      });
  }

  createDirectory(dir: string, name: string): Observable<any> {
    const params = new HttpParams().set('dir', dir).set('name', name);
    return this.http.post(
      LLMFILES_API + "/mkdir",
      null,
      {
        params
      });
  }

  reloadDocumentation(): Observable<any>{
    return this.http.post(
      LLMFILES_API + '/reload',
      null
    )
  }
}
