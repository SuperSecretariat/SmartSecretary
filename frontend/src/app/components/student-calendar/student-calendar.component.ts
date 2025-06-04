import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

interface Event {
  group: string;
  type: string;
  day: string;
  time: string; // Initial
  title: string;
  professor: string;
  startTime?: string; // Parsed
  endTime?: string;   // Parsed
}

interface BackendResponse {
  events: Event[];
}

interface ProcessedEvent extends Event {
  duration: number; // Number of hours the event spans
  startHour: number; // Starting hour as number
}

@Component({
  selector: 'app-student-calendar',
  standalone: false,
  templateUrl: './student-calendar.component.html',
  styleUrls: ['./student-calendar.component.css']
})
export class StudentCalendarComponent implements OnInit {
  days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  hours = this.generateHours(8, 19);
  showExams: boolean = false;
  annualView: boolean = false;

  ngOnInit() {
    // Optional: Set a default group if one isn't selected
    if (!this.selectedGroup || this.selectedGroup === '') {
      this.selectedGroup = '1A1';  // or any default you want
      // RAZVAN VILCU WILL HELP ME IMPLEMENT THIS BY CREATING A GROUP FOR STUDENTS
    }

    this.fetchEvents();
  }

  generateHours(start: number, end: number): string[] {
    const result: string[] = [];
    for (let i = start; i <= end; i++) {
      result.push(i.toString().padStart(2, '0') + ':00');
    }
    return result;
  }

  groups = ['1A1', '1A2', '1A3', '1A4', '1A5', '1B1', '1B2', '1B3', '1B4', '1E1', '1E2', '1E3', '1X1', '1X2', '1X3', '1X4',
    '2A1', '2A2', '2A3', '2A4', '2A5', '2B1', '2B2', '2B3', '2B4', '2E1', '2E2', '2E3', '2X1', '2X2', '2X3', '2X4',
    '3A1', '3A2', '3A3', '3A4', '3A5', '3B1', '3B2', '3B3', '3B4', '3E1', '3E2', '3E3', '3X1', '3X2', '3X3', '3X4'];
  selectedGroup = this.groups[0];
  events: Event[] = [];
  processedEvents: ProcessedEvent[] = [];

  constructor(private http: HttpClient) { }

  fetchEvents() {
    // const token = this.storageService.getUser().token;
    // const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg';
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    const body = new HttpParams().set('group', this.selectedGroup);

    this.http.post<any>(
        'http://localhost:8081/api/calendar/fetch-group',
        body.toString(),
        { headers }
    ).subscribe({
      next: (response: BackendResponse) => {
        console.log('✅ API response:', response);
        if (response && response.events) {
          this.events = response.events.map(event => {
            const [start, end] = event.time.split(' - ');
            return {
              ...event,
              startTime: start,
              endTime: end
            };
          });
          this.filterEvents();
          console.log('✅ Events loaded:', this.events);
        } else {
          console.error('No events found in the response', response);
          this.events = [];
          this.processedEvents = [];
        }
      },
      error: (error) => {
        console.error('Error fetching events:', error);
      }
    });
  }

  onGroupChange() {
    // Clear existing events immediately when group changes
    this.events = [];
    this.processedEvents = [];
    this.fetchEvents();
  }

  // Check if this is the first slot of an event (where we should render the event)
  shouldRenderEvent(day: string, hour: string): ProcessedEvent | null {
    const currentHour = parseInt(hour.split(':')[0]);

    const event = this.processedEvents.find(event =>
        event.day === day && event.startHour === currentHour
    );

    return event || null;
  }

  // Check if this slot is occupied by an event (but not the first slot)
  isEventContinuation(day: string, hour: string): boolean {
    const currentHour = parseInt(hour.split(':')[0]);

    return this.processedEvents.some(event =>
        event.day === day &&
        currentHour > event.startHour &&
        currentHour < event.startHour + event.duration
    );
  }

  toggleEventType() {
    this.showExams = !this.showExams;
    this.filterEvents();
  }

  toggleViewMode() {
    this.annualView = !this.annualView;
    // Implement logic for week/annual switching if necessary
  }

  filterEvents() {
    const filtered = this.showExams
      ? this.events.filter(e => e.type === 'Exam')
      : this.events.filter(e => e.type === 'Course' || e.type === 'Laboratory');

    this.processedEvents = filtered.map(e => this.toProcessedEvent(e));
  }


  toProcessedEvent(event: Event): ProcessedEvent {
    const [startHourStr, endHourStr] = (event.time || '').split(' - ') ?? ['00:00', '00:00'];
    const startHour = parseInt(startHourStr.split(':')[0]);
    const endHour = parseInt(endHourStr.split(':')[0]);
    const duration = endHour - startHour;

    return { ...event, startHour, duration };
  }

}
