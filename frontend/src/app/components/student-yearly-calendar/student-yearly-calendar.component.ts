import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface CalendarDay {
  date: number;
  month: number;
  year: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  isLearningDay?: boolean;
  isFreeDay?: boolean;
  isSessionDay?: boolean;
}

interface MonthData {
  name: string;
  year: number;
  month: number;
  days: CalendarDay[];
}

interface AcademicPeriod {
  id: number;
  start: string;
  end: string;
  type: 'Learning' | 'Free' | 'Session';
}

interface BackendResponse {
  message?: string;
  years?: AcademicPeriod[];
}

@Component({
  selector: 'app-student-yearly-calendar',
  standalone: false,
  templateUrl: './student-yearly-calendar.component.html',
  styleUrl: './student-yearly-calendar.component.css'
})
export class StudentYearlyCalendarComponent implements OnInit {
  weeklyView: boolean = false;
  months: MonthData[] = [];
  yearData: AcademicPeriod[] = [];
  loading: boolean = false;
  error: string = '';

  monthNames = [
    'October', 'November', 'December', 'January', 'February', 'March',
    'April', 'May', 'June', 'July', 'August', 'September'
  ];
  dayNames = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.generateAcademicYearCalendar();
    this.fetchYearlyCalendar();
  }

  toggleViewMode() {
    this.weeklyView = !this.weeklyView;
  }

  get annualView(): boolean {
    return !this.weeklyView;
  }

  fetchYearlyCalendar() {
    const token = 'your_token_here';

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.loading = true;
    this.error = '';

    this.http.post<BackendResponse>(
      'http://localhost:8081/api/calendar/yearly/fetch',
      {},
      { headers }
    ).subscribe({
      next: (response: BackendResponse) => {
        if (response?.years?.length) {
          this.yearData = response.years;
          this.generateAcademicYearCalendar();
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching calendar:', err);
        this.error = 'Failed to load calendar data';
        this.loading = false;
      }
    });
  }

  generateAcademicYearCalendar(): void {
    this.months = [];
    const today = new Date();

    let startYear = 2024;
    let endYear = 2025;

    if (this.yearData.length > 0) {
      const firstYear = this.yearData[0];
      const startDate = new Date(firstYear.start);
      const endDate = new Date(firstYear.end);

      if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime())) {
        startYear = startDate.getFullYear();
        endYear = endDate.getFullYear();
      }
    }

    const academicMonths = [
      { month: 9, year: startYear, name: 'October' },
      { month: 10, year: startYear, name: 'November' },
      { month: 11, year: startYear, name: 'December' },
      { month: 0, year: endYear, name: 'January' },
      { month: 1, year: endYear, name: 'February' },
      { month: 2, year: endYear, name: 'March' },
      { month: 3, year: endYear, name: 'April' },
      { month: 4, year: endYear, name: 'May' },
      { month: 5, year: endYear, name: 'June' },
      { month: 6, year: endYear, name: 'July' },
      { month: 7, year: endYear, name: 'August' },
      { month: 8, year: endYear, name: 'September' }
    ];

    academicMonths.forEach((monthInfo) => {
      const monthData: MonthData = {
        name: monthInfo.name,
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
    let startDate = new Date(firstDayOfMonth);
    const dayOfWeek = startDate.getDay();
    const daysToSubtract = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    startDate.setDate(startDate.getDate() - daysToSubtract);

    for (let i = 0; i < 42; i++) {
      const currentDate = new Date(startDate);
      currentDate.setDate(startDate.getDate() + i);

      const isLearningDay = this.isLearningDay(currentDate);
      const isSessionDay = this.isSessionDay(currentDate);
      const isFreeDay = this.isFreeDay(currentDate);

      const calendarDay: CalendarDay = {
        date: currentDate.getDate(),
        month: currentDate.getMonth(),
        year: currentDate.getFullYear(),
        isCurrentMonth: currentDate.getMonth() === month,
        isToday: this.isSameDate(currentDate, today) && currentDate.getMonth() === month,
        isFreeDay,
        isSessionDay,
        isLearningDay
      };

      days.push(calendarDay);
    }

    return days;
  }

  private isLearningDay(date: Date): boolean {
    return this.getPeriodTypeForDate(date) === 'Learning';
  }

  private isSessionDay(date: Date): boolean {
    return this.getPeriodTypeForDate(date) === 'Session';
  }

  private isFreeDay(date: Date): boolean {
    const isWeekend = date.getDay() === 0 || date.getDay() === 6;
    const isFreeFromAPI = this.getPeriodTypeForDate(date) === 'Free';
    return isWeekend || isFreeFromAPI;
  }


  private getPeriodTypeForDate(date: Date): string | null {
    for (const period of this.yearData) {
      const startParts = period.start.split('.');
      const endParts = period.end.split('.');

      const startDate = new Date(+startParts[2], +startParts[1] - 1, +startParts[0]);
      const endDate = new Date(+endParts[2], +endParts[1] - 1, +endParts[0]);

      if (date >= startDate && date <= endDate) {
        return period.type;
      }
    }
    return null;
  }


  private isSameDate(date1: Date, date2: Date): boolean {
    return date1.getFullYear() === date2.getFullYear()
      && date1.getMonth() === date2.getMonth()
      && date1.getDate() === date2.getDate();
  }

  getDayClasses(day: CalendarDay): string {
    const classes = ['calendar-day'];

    if (!day.isCurrentMonth) classes.push('other-month');
    if (day.isToday) classes.push('today');
    if (day.isSessionDay) classes.push('session-day');
    else if (day.isFreeDay) classes.push('free-day');
    else if (day.isLearningDay) classes.push('learning-day');

    return classes.join(' ');
  }

  retryFetchCalendar(): void {
    this.fetchYearlyCalendar();
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
