import { Component, OnInit } from '@angular/core';
import { DashboardComponent } from "./dashboard/dashboard.component";
import { FooterComponent } from "./footer/footer.component";
import { HeaderComponent } from "./header/header.component";
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { navbarVisibilityService } from './services/navbarVisibility.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [DashboardComponent, FooterComponent, HeaderComponent, NavBarComponent, CommonModule] 
})
export class AppComponent{
  title : string = 'Smart Secretary';

  constructor(private navbarVisibilityService : navbarVisibilityService) { }

  isNavBarVisible = false;

  ngOnInit() {
    this.navbarVisibilityService.isVisible$.subscribe(visibilityValue => {
      this.isNavBarVisible = visibilityValue;
    });
  }
}