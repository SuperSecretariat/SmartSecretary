import { Component, OnInit } from '@angular/core';
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { FooterComponent } from "./components/footer/footer.component";
import { HeaderComponent } from "./components/header/header.component";
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { NavBarVisibilityService } from './services/navbarVisibility.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [DashboardComponent, FooterComponent, HeaderComponent, NavBarComponent, CommonModule] 
})
export class AppComponent{
  title : string;
  navBarVisibilityService : NavBarVisibilityService;
  isNavBarVisible : boolean;

  constructor(private navBarVisibilityServiceParam : NavBarVisibilityService) { 
    this.navBarVisibilityService = navBarVisibilityServiceParam;
    this.title = 'Smart Secretary';
    this.isNavBarVisible = false;
  }

  ngOnInit() {
    this.navBarVisibilityService.getVisibility().subscribe(visibilityValue => {
      this.isNavBarVisible = visibilityValue;
    });
  }
}