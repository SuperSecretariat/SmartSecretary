import {Component, OnInit} from '@angular/core';
import { UserService } from '../../components/_services/user.service';


@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css'],
  standalone: false
})
export class BoardAdminComponent implements OnInit {
  content?: string;
  constructor(private readonly userService: UserService) { }

  ngOnInit(): void {
    this.userService.getAdminBoard().subscribe({
      next: data => {
        this.content = data;
      },
      error: err => {console.log(err)
        if (err.error) {
          this.content = JSON.parse(err.error).message;
        } else {
          this.content = "Error with status: " + err.status;
        }
      }
    });
  }
}
