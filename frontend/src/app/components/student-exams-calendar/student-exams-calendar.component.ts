import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

interface Exam {
  group: string;
  type: string;
  date: string;
  time: string; // Initial
  title: string;
  startTime?: string; // Parsed
  endTime?: string;   // Parsed
  parsedDate?: Date; // For exam calendar specific date handling
}

interface BackendResponse {
  exams: Exam[]; // Assuming the backend returns exams in the same format as events
}

interface ProcessedExam extends Exam {
  duration: number; // Number of hours the exam spans
  startHour: number; // Starting hour as number
  examDate: Date; // Specific date for the exam
}

@Component({
  selector: 'app-student-exams-calendar',
  standalone: false,
  templateUrl: './student-exams-calendar.component.html',
  styleUrl: './student-exams-calendar.component.css'
})
export class StudentExamsCalendarComponent implements OnInit {

  coursesView: boolean = false;

  // Group selection similar to calendar component
  groups = ['1A1', '1A2', '1A3', '1A4', '1A5', '1B1', '1B2', '1B3', '1B4', '1E1', '1E2', '1E3', '1X1', '1X2', '1X3', '1X4',
    '2A1', '2A2', '2A3', '2A4', '2A5', '2B1', '2B2', '2B3', '2B4', '2E1', '2E2', '2E3', '2X1', '2X2', '2X3', '2X4',
    '3A1', '3A2', '3A3', '3A4', '3A5', '3B1', '3B2', '3B3', '3B4', '3E1', '3E2', '3E3', '3X1', '3X2', '3X3', '3X4'];
  selectedGroup = this.groups[0];

  // Exam data
  exams: Exam[] = [];
  processedExams: ProcessedExam[] = [];
  hours = this.generateHours(8, 19);

  // Tooltip properties
  tooltipVisible: boolean = false;
  tooltipX: number = 0;
  tooltipY: number = 0;
  tooltipExams: ProcessedExam[] = [];

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    // Set default group if needed
    if (!this.selectedGroup || this.selectedGroup === '') {
      this.selectedGroup = '1A1';
    }

    this.fetchExams();
  }

  generateHours(start: number, end: number): string[] {
    const result: string[] = [];
    for (let i = start; i <= end; i++) {
      result.push(i.toString().padStart(2, '0') + ':00');
    }
    return result;
  }

  fetchExams() {
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    const body = new HttpParams().set('group', this.selectedGroup);

    this.http.post<any>(
      'http://localhost:8081/api/calendar/exam/fetch-group',
      body.toString(),
      { headers }
    ).subscribe({
      next: (response: BackendResponse) => {
        console.log('✅ API response for exams:', response);
        if (response && response.exams) {
          this.exams = response.exams
            .map(exam => {
              const [start, end] = exam.time.split(' - ');
              return {
                ...exam,
                startTime: start,
                endTime: end,
                parsedDate: this.parseExamDate(exam.date) // Convert day string to Date object
              };
            });
          this.processExams();
          console.log('✅ Exams loaded:', this.exams);
        } else {
          console.error('No exams found in the response', response);
          this.exams = [];
          this.processedExams = [];
        }
      },
      error: (error) => {
        console.error('Error fetching exams:', error);
      }
    });
  }

  onGroupChange() {
    // Clear existing exams immediately when group changes
    this.exams = [];
    this.processedExams = [];
    this.fetchExams();
  }

  processExams() {
    this.processedExams = this.exams.map(exam => this.toProcessedExam(exam));
  }

  toProcessedExam(exam: Exam): ProcessedExam {
    const [startHourStr, endHourStr] = (exam.time || '').split(' - ') ?? ['00:00', '00:00'];
    const startHour = parseInt(startHourStr.split(':')[0]);
    const endHour = parseInt(endHourStr.split(':')[0]);
    const duration = endHour - startHour;

    return {
      ...exam,
      startHour,
      duration,
      examDate: exam.parsedDate || new Date()
    };
  }

  parseExamDate(dayString: string | undefined): Date {
    if (!dayString) {
      console.warn('⚠️ Missing exam day');
      return new Date(); // fallback
    }

    // Check if it's in DD.MM.YYYY format
    const dateParts = dayString.split('.');
    if (dateParts.length === 3) {
      const [day, month, year] = dateParts.map(Number);
      return new Date(year, month - 1, day); // JS months are 0-based
    }

    console.warn(`⚠️ Unsupported date format: "${dayString}"`);
    return new Date(); // fallback
  }


  // Check if an exam exists on a specific date
  getExamsForDate(date: Date): ProcessedExam[] {
    return this.processedExams.filter(exam => {
      const examDate = exam.examDate;
      return examDate.getDate() === date.getDate() &&
        examDate.getMonth() === date.getMonth() &&
        examDate.getFullYear() === date.getFullYear();
    });
  }

  toggleViewMode() {
    this.coursesView = !this.coursesView;
    // Implement logic for courses/exam switching if necessary
    if (this.coursesView) {
      // Fetch courses instead of exams
      // You might want to create a separate fetchCourses method
    } else {
      this.fetchExams();
    }
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

  hideTooltip() {
    this.tooltipVisible = false;
    this.tooltipExams = [];
  }

  onDayMouseEnter(event: MouseEvent, date: Date) {
    const examsForDate = this.getExamsForDate(date);
    if (examsForDate.length > 0) {
      // Show tooltip for days with exams
      this.tooltipExams = examsForDate;
      this.tooltipX = event.clientX + 10;
      this.tooltipY = event.clientY + 10;
      this.tooltipVisible = true;
    } else {
      // Hide tooltip when entering a day without exams
      this.hideTooltip();
    }
  }

  formatExamDate(date: Date): string {
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
