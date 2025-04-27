import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MenuItem } from '../models/menu-item.model';
import { CommonModule } from '@angular/common';
import { OnInit } from '@angular/core';
import { RouterLink, RouterOutlet} from '@angular/router';
import { navbarVisibilityService } from '../services/navbarVisibility.service';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    CommonModule,
    RouterLink,
    RouterOutlet
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})


export class HeaderComponent implements OnInit {
  showFiller = true;
  private menuItems: MenuItem[] = [];

  constructor(private navbarVisibilityService: navbarVisibilityService) {}

  ngOnInit() {
    
    // Initialize the menu items here or fetch them from a service
    this.menuItems = [
      new MenuItem('Home', '/home'),
      new MenuItem('About', '/about'),
      new MenuItem('Contact', '/contact'),
      new MenuItem('Services', '/services'),
      new MenuItem('Produces', '/products')
    ]
  }

  toggleOnNavBar(): void {
    this.navbarVisibilityService.showComponent();
  }

  toggleOffNavBar(): void {
    this.navbarVisibilityService.hideComponent();
  }

  getMenuItems(): MenuItem[] {
    return this.menuItems;
  }

}
