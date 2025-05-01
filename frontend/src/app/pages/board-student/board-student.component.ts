import { Component, OnInit } from '@angular/core';
import { UserService } from '../../components/_services/user.service';
import { DashboardComponent } from "../../components/dashboard/dashboard.component";
import { FooterComponent } from "../../components/footer/footer.component";
import { HeaderComponent } from "../../components/header/header.component";
import { NavBarComponent } from '../../components/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { NavBarVisibilityService } from '../../components/_services/navbarVisibility.service';
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