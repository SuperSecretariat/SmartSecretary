
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { StorageService } from '../_services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class StudentGuard implements CanActivate {
  
  constructor(private readonly storageService: StorageService, private readonly router: Router) {}
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    
    if (this.storageService.isStudent()) {
      return true;
    }
    
    if (this.storageService.isSecretary()) {
      this.router.navigate(['/secretary/newsfeed'], { queryParams: { returnUrl: state.url } });
    }

    if (this.storageService.isAdmin()) {
      this.router.navigate(['/admin/newsfeed'], { queryParams: { returnUrl: state.url } });
    }
  
    return false;
  }
}