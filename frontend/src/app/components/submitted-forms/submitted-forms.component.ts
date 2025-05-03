import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { SearchBarComponent } from '../dashboard/search-bar/search-bar.component';

@Component({
  selector: 'app-submitted-forms',
  imports: [
    SearchBarComponent
  ],
  templateUrl: './submitted-forms.component.html',
  styleUrl: './submitted-forms.component.css'
})
export class SubmittedFormsComponent implements OnInit{
  ngOnInit(): void {
    // Initialize the component and set up any necessary data or state
    // For example, you can fetch menu items from a service or define them here
    console.log('SubmittedForms component initialized');
  }
}
