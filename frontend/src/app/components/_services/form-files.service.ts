import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';

const BASE_DIR = '/formDocuments';
const FILES_API = `${environment.backendUrl}/api/files`;

export interface FileEntry {
  name: string;
  isDirectory: boolean;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class FormFilesService {

  constructor(private http: HttpClient) {}

  listFiles(subPath: string = ''): Observable<FileEntry[]> {
    let params = new HttpParams();
    if (subPath) params = params.set('subPath', subPath);
    return this.http.get<FileEntry[]>(
      FILES_API + '/list' + BASE_DIR,
      { params }
    );
  }

  uploadDocument(file: File, subPath: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (subPath) formData.append('subPath', subPath);
    return this.http.post(
      FILES_API + '/uploadDocument' + BASE_DIR,
      formData
    );
  }

  downloadFile(subPath: string, name: string): Observable<Blob> {
    let params = new HttpParams().set('file', name);
    if (subPath) params = params.set('subPath', subPath);
    return this.http.get(
      FILES_API + '/download' + BASE_DIR,
      {
        params, responseType: 'blob'
      },
    );
  }

  deleteEntry(subPath: string, name: string): Observable<any> {
    let params = new HttpParams().set('name', name);
    if (subPath) params = params.set('subPath', subPath);
    return this.http.delete(
      FILES_API + '/delete' + BASE_DIR,
      {
        params
      });
  }

  createDirectory(subPath: string, name: string): Observable<any> {
    let params = new HttpParams().set('name', name);
    if (subPath) params = params.set('subPath', subPath);
    return this.http.post(
      FILES_API + '/mkdir' + BASE_DIR,
      null,
      {
        params
      });
  }

}
