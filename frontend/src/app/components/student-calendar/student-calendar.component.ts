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


@Component({
  selector: 'app-student-calendar',
  standalone: false,
  templateUrl: './student-calendar.component.html',
  styleUrls: ['./student-calendar.component.css']
})
export class StudentCalendarComponent {
  days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  hours = this.generateHours(8, 19);
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

  constructor(private http: HttpClient) { }
  ngOnInit() {
      this.fetchEvents();
  }

  fetchEvents() {
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzQ4MTgwMjczLCJleHAiOjE3NDgyNjY2NzN9._JMy_9-lPdNBc3k-P22x_S7dCTSqjkN7Jx13RVYrLMg'; // Replace with actual token
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
          console.log('✅ Events loaded:', this.events);
        } else {
          console.error('No events found in the response', response);
          this.events = [];
        }
      }
      ,
      error: (error) => {
        console.error('Error fetching events:', error);
      }
    });
  }

    onGroupChange() {
      this.fetchEvents();
    }

  getEventForSlot(day: string, hour: string): Event | undefined {
    return this.events.find(event => {
      if (event.day !== day) return false;

      const startParts = event.startTime?.split(':') ?? [];
      const endParts = event.endTime?.split(':') ?? [];
      const hourParts = hour.split(':');

      const startHour = parseInt(startParts[0]);
      const endHour = parseInt(endParts[0]);
      const currentHour = parseInt(hourParts[0]);

      return currentHour >= startHour && currentHour < endHour;
    });
  }





}
