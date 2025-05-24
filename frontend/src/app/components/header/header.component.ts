import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { CommonModule } from '@angular/common';
import { NavBarVisibilityService } from '../_services/navbarVisibility.service';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { Router,NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    CommonModule
  ]
})
export class HeaderComponent implements OnInit {
  showFiller = true;
  isLoggedIn: boolean = false;
  navBarVisibilityService: NavBarVisibilityService;
  currentTitle: string = 'SmartSecretary';

  constructor(
    navBarVisibilityServiceParam: NavBarVisibilityService,
    private readonly authService: AuthService,
    private readonly storageService: StorageService,
    private readonly router: Router
  ) {
    this.navBarVisibilityService = navBarVisibilityServiceParam;
  }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        const url = this.router.url;

        
        if (url.includes('/create-form')) this.currentTitle = 'Create Form';
        else if (url.includes('/submitted-forms')) this.currentTitle = 'Submitted Forms';
        else if (url.includes('/account')) this.currentTitle = 'Account Manager';
        else if (url.includes('/newsfeed')) this.currentTitle = 'Newsfeed';
        
        else if (url.includes('/student')) this.currentTitle = 'Student Dashboard';
        else if (url.includes('/login')) this.currentTitle = 'Login';
        else if (url.includes('/register')) this.currentTitle = 'Register';
        else this.currentTitle = 'SmartSecretary';
      });
  }

  toggleNavBar(): void {
    this.navBarVisibilityService.switchVisibility();
  }

  logout(): void {
  this.authService.logout().subscribe({
    next: response => {
      console.log('Logout successful:', response);
      this.storageService.clean();
      this.router.navigate(['/login']).then(() => {
        window.location.reload();
      });
    },
    error: err => {
      console.error('Logout error:', err);
      this.storageService.clean();
      this.router.navigate(['/newsfeed']).then(() => {
        window.location.reload();
      });
    }
  });
}
}
