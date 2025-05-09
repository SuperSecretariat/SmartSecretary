import { Component, OnInit } from '@angular/core';
import { UserService } from '../../components/_services/user.service';

@Component({
  selector: 'app-board-secretary',
  templateUrl: './board-secretary.component.html',
  styleUrls: ['./board-secretary.component.css'],
  standalone: false
})
export class BoardSecretaryComponent implements OnInit {
  content?: string;

  constructor(private readonly userService: UserService) { }

  ngOnInit(): void {
    this.userService.getSecretaryBoard().subscribe({
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
