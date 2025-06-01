import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { StorageService } from '../../../components/_services/storage.service';
import { FormsService } from '../../../components/_services/forms.service';

@Component({
  selector: 'app-board-secretary-review-ticket',
  standalone: false,
  templateUrl: './review-request.component.html',
  styleUrl: './review-request.component.css'
})
export class ReviewRequestComponent implements OnInit, OnDestroy {
  selectedFormId: number | null = null;
  imageUrl: SafeUrl | null = null;
  isLoading = false;
  statusMessage = '';

  private statusChanged = false;

  
  constructor(
    private readonly storageService: StorageService,
    private readonly http: HttpClient,
    private readonly sanitizer: DomSanitizer,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly formsService: FormsService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.selectedFormId = +idParam;
        this.setInReviewStatus(); // setează statusul IN_REVIEW la vizualizare
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

  setInReviewStatus() {
    if (this.selectedFormId) {
      this.isLoading = true;
      this.formsService.updateFormStatusById(this.selectedFormId, 'IN_REVIEW').subscribe({
        next: () => { this.isLoading = false; },
        error: () => { this.isLoading = false; }
      });
    }
  }

  updateStatus(status: string) {
    if (!this.selectedFormId) return;
    this.isLoading = true;
    this.formsService.updateFormStatusById(this.selectedFormId, status).subscribe({
      next: () => {
        this.statusMessage = `Status updated to ${status.replace('_', ' ')}`;
        this.isLoading = false;
        this.statusChanged = true;
        this.router.navigate([`secretary/dashboard/viewTickets`])
      },
      error: () => {
        this.statusMessage = 'Failed to update status';
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (!this.statusChanged && this.selectedFormId) {
      this.formsService.updateFormStatusById(this.selectedFormId, 'PENDING').subscribe();
    }
  }
}
