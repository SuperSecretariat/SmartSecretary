import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { DashboardComponent } from "../dashboard/dashboard.component";
import { FooterComponent } from "../footer/footer.component";
import { HeaderComponent } from "../header/header.component";
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { NavBarVisibilityService } from '../_services/navbarVisibility.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './board-student.component.html',
  styleUrls: ['./board-student.component.css'],
  imports: [DashboardComponent, FooterComponent, HeaderComponent, NavBarComponent, CommonModule],
})
export class BoardStudentComponent{
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
      console.log(visibilityValue);
    });
  }
}