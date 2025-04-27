import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { NavBarItem } from '../../models/nav-bar-item.model';

@Component({
  selector: 'app-nav-bar',
  imports: [],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
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
    console.log('NavBar component initialized');
  }
}
