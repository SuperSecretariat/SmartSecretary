import { Component,OnInit } from '@angular/core';
import { NavBarItem } from '../models/nav-bar-item.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'admin-nav-bar',
  templateUrl: './admin-nav-bar.component.html',
  styleUrl: './admin-nav-bar.component.css',
  imports: [CommonModule, RouterModule],
  standalone: true
})

export class AdminNavBarComponent implements OnInit {
  navBarItems: NavBarItem[] = [];
  constructor() {}

  ngOnInit(): void {
    this.navBarItems.push(new NavBarItem('Add', `/admin/dashboard/add`));
    this.navBarItems.push(new NavBarItem('Show Auth Key', `/admin/dashboard/showKey`));
    this.navBarItems.push(new NavBarItem('Change ChatBot documents', `/admin/dashboard/llmFiles`));
    this.navBarItems.push(new NavBarItem('Delete User', `/admin/dashboard/delete`));
  }

  getNavBarItems(): { name: string; route: string }[] {
    return this.navBarItems.map(item => ({
      name: item.getName(),
      route: item.getRoute()
    }));
  }
}

