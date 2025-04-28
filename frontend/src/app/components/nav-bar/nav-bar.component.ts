import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { NavBarItem } from '../../models/nav-bar-item.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  imports: [CommonModule, RouterModule],
})
export class NavBarComponent implements OnInit{
  navBarItems: NavBarItem[];

  constructor() {
    this.navBarItems= [];
  }

  ngOnInit(): void {
    // Dynamically populate the items of the NavBar

    // Initialize the menu items here or fetch them from a service
    this.navBarItems = [
      new NavBarItem('Home', '/home'),
      new NavBarItem('About', '/about'),
      new NavBarItem('Contact', '/contact'),
      new NavBarItem('Services', '/services'),
      new NavBarItem('Produces', '/products')
    ]
    // console.log('NavBar component initialized');
  }

  getNavBarItems(): { name: string, route: string }[] {
      return this.navBarItems.map(item => ({
        name: item.getName(),
        route: item.getRoute()
      }));
  }
}
