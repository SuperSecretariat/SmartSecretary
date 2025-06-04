import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { StorageService } from '../_services/storage.service';
import { FormsService } from '../_services/forms.service';

@Component({
  selector: 'app-view-form',
  standalone: false,
  templateUrl: './view-form.component.html',
  styleUrl: './view-form.component.css'
})
export class ViewFormComponent implements OnInit {
  selectedFormId: number | null = null;
  formRequestImagesUrl: string[] = [];

  constructor(
    private readonly storageService: StorageService,
    private readonly formsService: FormsService,
    private readonly http: HttpClient,
    private readonly sanitizer: DomSanitizer,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.selectedFormId = +idParam;
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
}
