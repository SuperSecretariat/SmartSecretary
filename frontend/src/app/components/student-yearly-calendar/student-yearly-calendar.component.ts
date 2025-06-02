import { Component } from '@angular/core';

interface CalendarDay {
  date: number;
  month: number;
  year: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  isLearningDay?: boolean; // Optional property for learning days
  isFreeDay?: boolean; // Optional property for free days
  isSessionDay?: boolean; // Optional property for session days
}

interface MonthData {
  name: string;
  year: number;
  month: number;
  days: CalendarDay[];
}

@Component({
  selector: 'app-student-yearly-calendar',
  standalone: false,
  templateUrl: './student-yearly-calendar.component.html',
  styleUrl: './student-yearly-calendar.component.css'
})
export class StudentYearlyCalendarComponent {
  weeklyView: boolean = false;


  toggleViewMode() {
    this.weeklyView = !this.weeklyView;
  }
  ngOnInit(): void {
    this.generateAcademicYearCalendar();
  }
  months: MonthData[] = [];
  monthNames = [
    'October', 'November', 'December', 'January', 'February', 'March',
    'April', 'May', 'June', 'July', 'August', 'September'
  ];
  dayNames = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];


  generateAcademicYearCalendar(): void {
    this.months = [];
    const today = new Date();

    // Academic year: October 2024 to September 2025
    const academicMonths = [
      { month: 9, year: 2024 },  // October 2024
      { month: 10, year: 2024 }, // November 2024
      { month: 11, year: 2024 }, // December 2024
      { month: 0, year: 2025 },  // January 2025
      { month: 1, year: 2025 },  // February 2025
      { month: 2, year: 2025 },  // March 2025
      { month: 3, year: 2025 },  // April 2025
      { month: 4, year: 2025 },  // May 2025
      { month: 5, year: 2025 },  // June 2025
      { month: 6, year: 2025 },  // July 2025
      { month: 7, year: 2025 },  // August 2025
      { month: 8, year: 2025 }   // September 2025
    ];

    academicMonths.forEach((monthInfo, index) => {
      const monthData: MonthData = {
        name: this.monthNames[index],
        year: monthInfo.year,
        month: monthInfo.month,
        days: this.generateMonthDays(monthInfo.year, monthInfo.month, today)
      };
      this.months.push(monthData);
    });
  }

  generateMonthDays(year: number, month: number, today: Date): CalendarDay[] {
    const days: CalendarDay[] = [];
    const firstDayOfMonth = new Date(year, month, 1);

    // Get the first Monday of the calendar grid
    let startDate = new Date(firstDayOfMonth);
    const dayOfWeek = firstDayOfMonth.getDay();
    const daysToSubtract = dayOfWeek === 0 ? 6 : dayOfWeek - 1; // Convert Sunday=0 to Monday=0
    startDate.setDate(startDate.getDate() - daysToSubtract);

    // Generate 42 days (6 weeks) to fill the calendar grid
    for (let i = 0; i < 42; i++) {
      const currentDate = new Date(startDate);
      currentDate.setDate(startDate.getDate() + i);

      const isFreeDay = currentDate.getDay() === 0 || currentDate.getDay() === 6; // Sunday=0, Saturday=6

      const calendarDay: CalendarDay = {
        date: currentDate.getDate(),
        month: currentDate.getMonth(),
        year: currentDate.getFullYear(),
        isCurrentMonth: currentDate.getMonth() === month,
        isToday: this.isSameDate(currentDate, today) && currentDate.getMonth() === month,
        isFreeDay : isFreeDay
      };
      days.push(calendarDay);
    }

    return days;
  }

  private isSameDate(date1: Date, date2: Date): boolean {
    return date1.getFullYear() === date2.getFullYear() &&
      date1.getMonth() === date2.getMonth() &&
      date1.getDate() === date2.getDate();
  }

  getDayClasses(day: CalendarDay): string {
    const classes = ['calendar-day'];

    if (!day.isCurrentMonth) {
      classes.push('other-month');
    }

    if (day.isToday) {
      classes.push('today');
    }

    if(day.isFreeDay) {
      classes.push('free-day');
    }

    if (day.isLearningDay) {
      classes.push('learning-day');
    }

    if (day.isSessionDay) {
      classes.push('session-day');
    }



    return classes.join(' ');
  }

}
