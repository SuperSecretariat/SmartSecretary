import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `<h1>Smart Secretary</h1>`,
})
export class AppComponent implements OnInit {
  ngOnInit(): void {
    fetch('http://localhost:8081/api/ping')
      .then(response => {
        if (response.ok) {
          console.log('✅ Connected to backend');
        } else {
          console.log('❌ Backend responded but with error');
        }
      })
      .catch(error => {
        console.log('❌ Could not connect to backend', error);
      });
  }
}