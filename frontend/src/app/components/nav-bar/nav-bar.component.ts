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
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('Admin Dashboard', `/${rolePrefix}/dashboard`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
      ];
    else if(roles.includes("ROLE_SECRETARY"))
      this.navBarItems = [
        new NavBarItem('Secretary Dashboard', `/${rolePrefix}/dashboard`),
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
      ];
    else // STUDENT
      this.navBarItems = [
        new NavBarItem('Create form', `/${rolePrefix}/create-form`),
        new NavBarItem('Submitted forms', `/${rolePrefix}/submitted-forms`),
        new NavBarItem('Account Manager', `/${rolePrefix}/account`),
        new NavBarItem('News Feed', `/${rolePrefix}/newsfeed`),
        new NavBarItem('Pubble AI', `/${rolePrefix}/pubble`),
        new NavBarItem('Calendar', `/${rolePrefix}/calendar`),
        new NavBarItem('Tickets', `/${rolePrefix}/tickets`),
      ];

  }

  getNavBarItems(): { name: string; route: string }[] {
    return this.navBarItems.map(item => ({
      name: item.getName(),
      route: item.getRoute()
    }));
  }


}

