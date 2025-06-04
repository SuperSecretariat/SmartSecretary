
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { StorageService } from '../_services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class SecretaryGuard implements CanActivate {
  
  constructor(private readonly storageService: StorageService, private readonly router: Router) {}
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    
    if (this.storageService.isSecretary()) {
      return true;
    }
    
    if (this.storageService.isAdmin()) {
      this.router.navigate(['/admin/newsfeed'], { queryParams: { returnUrl: state.url } });
    }

    if (this.storageService.isStudent()) {
      this.router.navigate(['/student/newsfeed'], { queryParams: { returnUrl: state.url } });
    }
  
    return false;
  }
}