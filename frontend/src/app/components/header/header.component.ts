import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { CommonModule } from '@angular/common';
import { OnInit } from '@angular/core';
import { RouterLink, RouterOutlet} from '@angular/router';
import { NavBarVisibilityService } from '../../services/navbarVisibility.service';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    CommonModule,
    // RouterLink,
    // RouterOutlet
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})


export class HeaderComponent implements OnInit {
  showFiller = true;
  navBarVisibilityService : NavBarVisibilityService;

  constructor(private navBarVisibilityServiceParam: NavBarVisibilityService) {
    this.navBarVisibilityService = navBarVisibilityServiceParam;
  }

  ngOnInit() {
    // Initialize the component and set up any necessary data or state
    // For example, you can fetch menu items from a service or define them here
    console.log('Header component initialized');
  }

  toggleNavBar(): void {
    this.navBarVisibilityService.switchVisibility();
  }

}
