import { Component } from '@angular/core';
import { StorageService } from './components/_services/storage.service';
import { AuthService } from './components/_services/auth.service';
import { NavBarVisibilityService } from './components/_services/navbarVisibility.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false
})
export class AppComponent {
  private roles: string[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  showSecretaryBoard = false;
  showStudentBoard = false;
  username?: string;
  isNavBarVisible = false;

  constructor(
    private readonly storageService: StorageService,
    private readonly authService: AuthService,
    private readonly navBarVisibilityService: NavBarVisibilityService) 
    { this.navBarVisibilityService.getVisibility().subscribe(value => {
      this.isNavBarVisible = value;});
    }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;

      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showSecretaryBoard = this.roles.includes('ROLE_SECRETARY');
      this.showStudentBoard = this.roles.includes('ROLE_STUDENT');

      this.username = user.username;
    }
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: res => {
        console.log(res);
        this.storageService.clean();

        window.location.reload();
      },
      error: err => {
        console.log(err);
      }
    });
  }
}