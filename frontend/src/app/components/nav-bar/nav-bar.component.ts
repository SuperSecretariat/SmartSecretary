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

  const user = this.storageService.getUser();
  const roles: string[] = user.roles;
  const rolePrefix = roles[0].replace('ROLE_', '').toLowerCase();

    if(roles.includes("ROLE_ADMIN"))
      this.navBarItems = [
        new NavBarItem('Home', `/${rolePrefix}/home`),
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('Admin Dashboard', `/${rolePrefix}/dashboard`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
      ];
    else if(roles.includes("ROLE_SECRETARY"))
      this.navBarItems = [
        new NavBarItem('Home', `/${rolePrefix}/home`),
        new NavBarItem('Secretary Dashboard', `/${rolePrefix}/dashboard`),
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
      ];
    else // STUDENT
      this.navBarItems = [
        new NavBarItem('Home', `/${rolePrefix}/home`),
        new NavBarItem('Create form', `/${rolePrefix}/create-form`),
        new NavBarItem('Submitted forms', `/${rolePrefix}/submitted-forms`),
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
        new NavBarItem('Create Ticket', `/${rolePrefix}/ticket-student`),
        new NavBarItem('Pubble AI', `/${rolePrefix}/pubble`)
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

