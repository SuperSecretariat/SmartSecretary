import { Component,OnInit } from '@angular/core';
import { NavBarItem } from '../models/nav-bar-item.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  imports: [CommonModule, RouterModule],
  standalone: true
})

export class NavBarComponent implements OnInit {
  navBarItems: NavBarItem[] = [];
  isLoggedIn: boolean = false;

  constructor(
    private storageService: StorageService,
  ) {}

  ngOnInit(): void {

    this.isLoggedIn = this.storageService.isLoggedIn();

    if(!this.isLoggedIn){
      this.navBarItems=[
        new NavBarItem('Home', '/home'),
        new NavBarItem('Login', '/login'),
        new NavBarItem('Sign up', '/register'),
      ];
      return;
    }

    this.navBarItems = [
      new NavBarItem('Home', '/home'),
      new NavBarItem('Create form', '/create-form'),
      new NavBarItem('Submitted forms', '/submitted-forms'),
      new NavBarItem('Account Manager', '/account'),
      new NavBarItem('News Feed', '/newsfeed'),
    ];

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      const roles: string[] = user.roles;

      for (const role of roles) {
        if (role.startsWith('ROLE_')) {
          const roleName = role.replace('ROLE_', '').toLowerCase();
          const capitalized = roleName.charAt(0).toUpperCase() + roleName.slice(1);
          this.navBarItems.push(
            new NavBarItem(`${capitalized} Announcements`, `/${roleName}/announcements`)
          );
        }
      }
    }

  }

  getNavBarItems(): { name: string; route: string }[] {
    return this.navBarItems.map(item => ({
      name: item.getName(),
      route: item.getRoute()
    }));
  }

  
}

