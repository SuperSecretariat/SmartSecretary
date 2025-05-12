import { Component } from '@angular/core';
import {NavBarItem} from '../models/nav-bar-item.model';

@Component({
  selector: 'app-secretary-nav-bar',
  standalone: false,
  templateUrl: './secretary-nav-bar.component.html',
  styleUrl: './secretary-nav-bar.component.css'
})
export class SecretaryNavBarComponent {
  navBarItems: NavBarItem[] = [];
  constructor() {}

  ngOnInit(): void {
    this.navBarItems.push(new NavBarItem('Add', `/secretary/dashboard/add`));
    this.navBarItems.push(new NavBarItem('View Student Tickets', `/secretary/dashboard/viewTickets`));
    this.navBarItems.push(new NavBarItem('Add Document', `/secretary/dashboard/addDocument`));
    this.navBarItems.push(new NavBarItem('Delete Student', ``)); //to be implemented
  }

  getNavBarItems(): { name: string; route: string }[] {
    return this.navBarItems.map(item => ({
      name: item.getName(),
      route: item.getRoute()
    }));
  }
}
