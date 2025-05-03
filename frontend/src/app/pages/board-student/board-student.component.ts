import { Component } from '@angular/core';
import { DashboardComponent } from "../../components/dashboard/dashboard.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-board-student',
  templateUrl: './board-student.component.html',
  styleUrls: ['./board-student.component.css'],
  standalone: true,
  imports: [DashboardComponent, CommonModule]
})
export class BoardStudentComponent {}
