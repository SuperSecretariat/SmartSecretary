import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-student-exams-calendar',
  standalone: false,
  templateUrl: './student-exams-calendar.component.html',
  styleUrl: './student-exams-calendar.component.css'
})
export class StudentExamsCalendarComponent implements OnInit {

  coursesView: boolean = false;

  toggleViewMode() {
    this.coursesView = !this.coursesView;
    // Implement logic for courses/exam switching if necessary
  }

  examPeriods = [
    {
      title: 'Exam Session January–February',
      start: new Date(2025, 0, 20),  // Jan 20
      end: new Date(2025, 1, 23)     // Feb 23
    },
    {
      title: 'Exam Session June–July',
      start: new Date(2025, 5, 9),   // June 9
      end: new Date(2025, 6, 13)     // July 13
    }
  ];

  getDaysInPeriod(start: Date, end: Date): Date[] {
    const days: Date[] = [];
    const date = new Date(start);
    while (date <= end) {
      days.push(new Date(date));
      date.setDate(date.getDate() + 1);
    }
    return days;
  }

  getWeekdayName(date: Date): string {
    return date.toLocaleDateString('en-US', { weekday: 'short' });
  }

  getDayNumber(date: Date): number {
    return date.getDate();
  }

  ngOnInit(): void {
  }


}
