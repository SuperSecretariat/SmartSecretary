import { Component } from '@angular/core';
import { DashboardComponent } from "../../components/dashboard/dashboard.component";
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-board-student',
  templateUrl: './board-student.component.html',
  styleUrls: ['./board-student.component.css'],
  standalone: true,
  imports: [DashboardComponent, CommonModule, RouterOutlet]
})
export class BoardStudentComponent {}
