import { Component, OnInit } from '@angular/core';
import { DashboardComponent } from "./dashboard/dashboard.component";
//import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { FooterComponent } from "./footer/footer.component";
import { HeaderComponent } from "./header/header.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [DashboardComponent, FooterComponent, HeaderComponent] //NavBarComponent
})
export class AppComponent{
  title : string = 'Smart Secretary';
}