import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { SearchBarComponent } from './search-bar/search-bar.component';

@Component({
  selector: 'app-dashboard',
  imports: [
    SearchBarComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit{

  ngOnInit(): void {
    // Initialize the component and set up any necessary data or state
    // For example, you can fetch menu items from a service or define them here
    console.log('Dashboard component initialized');
  }
}
