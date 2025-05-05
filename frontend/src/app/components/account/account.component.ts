import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-account',
  imports: [],
  templateUrl: './account.component.html',
  styleUrl: './account.component.css'
})
export class AccountComponent {
  currentUser: any;
  
    constructor(private storageService: StorageService) { }
  
    ngOnInit(): void {
      this.storageService.getUserProfile().subscribe({
        next: data => this.currentUser = data,
        error: err => console.error('Failed to fetch user profile', err)
      });
    }
}
