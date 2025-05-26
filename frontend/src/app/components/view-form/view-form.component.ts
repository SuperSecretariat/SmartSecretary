import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environments';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-view-form',
  standalone: false,
  templateUrl: './view-form.component.html',
  styleUrl: './view-form.component.css'
})
export class ViewFormComponent implements OnInit {
  selectedFormId: number | null = null;
  imageUrl: SafeUrl | null = null;

  constructor(
    private storageService: StorageService,
    private http: HttpClient,
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.selectedFormId = +idParam;
        this.loadFormImage();
      }
    });
  }

  loadFormImage(): void {
    const token = this.storageService.getUser()?.token;
    if (!token || !this.selectedFormId) return;

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const imageUrl = `${environment.backendUrl}/api/form-requests/${this.selectedFormId}/image`;

    this.http.get(imageUrl, { headers, responseType: 'blob' }).subscribe({
      next: (blob: Blob) => {
        const objectURL = URL.createObjectURL(blob);
        this.imageUrl = this.sanitizer.bypassSecurityTrustUrl(objectURL);
      },
      error: err => {
        console.error('Failed to load image', err);
      }
    });
  }
}
