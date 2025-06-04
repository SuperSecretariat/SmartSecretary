import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient} from '@angular/common/http';
import { DomSanitizer} from '@angular/platform-browser';
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
  isLoading = false;
  statusMessage = '';
  formRequestImagesUrl: string[] = [];

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
        this.loadFormImages(this.selectedFormId);
      }
    });
  }

  loadFormImages(id: number | null): void {
    if (id){
      this.formsService.getFormRequestImages(id).subscribe({
        next: (data: string[]) => {
          this.formRequestImagesUrl = data.map(base64 => 'data:image/png;base64,' + base64);
        },
        error: error => {
          console.log("Error fetching formRequest images: " + error);
        }
      })
    }
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
