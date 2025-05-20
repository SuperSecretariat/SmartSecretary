import { Component } from '@angular/core';
import { Breakpoints } from '@angular/cdk/layout';
import { BreakpointObserver } from '@angular/cdk/layout';
import { OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent implements OnInit {
  isGdprVisible: boolean;
  breakpointObserver: BreakpointObserver;


  constructor(private breakpointObserverParam: BreakpointObserver) {
    this.breakpointObserver = breakpointObserverParam;
    this.isGdprVisible = true; // Default value
  }

  ngOnInit(): void {
    this.breakpointObserver.observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape, Breakpoints.WebPortrait, Breakpoints.WebLandscape]).subscribe(result => {
      const breakpoints = result.breakpoints;

      // Check the current breakpoint and set isGdprVisible accordingly
      // Add breakpoints as you need so you can handle more screen sizes
      if (breakpoints[Breakpoints.WebLandscape]) {
        this.isGdprVisible = true;
        // console.log('Web Landscape');
      } else if (breakpoints[Breakpoints.WebPortrait]) {
        this.isGdprVisible = true;
        // console.log('Web Portrait');
      } else if (breakpoints[Breakpoints.HandsetLandscape]) {
        this.isGdprVisible = true;
        // console.log('Handset Landscape');
      } else if (breakpoints[Breakpoints.HandsetPortrait]) {
        this.isGdprVisible = false;
        // console.log('Handset Portrait');
      }
    });
    // console.log('Footer component initialized');
  }
}
