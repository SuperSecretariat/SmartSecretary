import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-news-item',
  standalone: true,
  templateUrl: './news-item.component.html',
  styleUrls: ['./news-item.component.css'],
  imports: [CommonModule]
})
export class NewsItemComponent {
  @Input() id!: number;
  @Input() formName!: string;
  @Input() status!: string;
  @Input() message!: string;
}