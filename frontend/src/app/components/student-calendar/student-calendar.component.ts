import { Component } from '@angular/core';

@Component({
  selector: 'app-student-calendar',
  standalone: false,
  templateUrl: './student-calendar.component.html',
  styleUrls: ['./student-calendar.component.css']
})
export class StudentCalendarComponent {
  days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  hours = this.generateHours(8, 19);

  events: Event[] = [];

  groups = ['1A1', '1A2', '1A3', '1A4', '1A5', '1B1', '1B2', '1B3', '1B4', '1E1', '1E2', '1E3', '1X1', '1X2', '1X3', '1X4',
                    '2A1', '2A2', '2A3', '2A4', '2A5', '2B1', '2B2', '2B3', '2B4', '2E1', '2E2', '2E3', '2X1', '2X2', '2X3', '2X4',
                    '3A1', '3A2', '3A3', '3A4', '3A5', '3B1', '3B2', '3B3', '3B4', '3E1', '3E2', '3E3', '3X1', '3X2', '3X3', '3X4'];
  selectedGroup = this.groups[0];

  generateHours(start: number, end: number): string[] {
    const result: string[] = [];
    for (let i = start; i <= end; i++) {
      result.push(i.toString().padStart(2, '0') + ':00');
    }
    return result;
  }

}
