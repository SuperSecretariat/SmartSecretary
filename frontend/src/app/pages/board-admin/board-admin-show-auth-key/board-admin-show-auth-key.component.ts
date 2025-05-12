import {Component} from '@angular/core';

@Component({
  selector: 'app-board-admin-show-auth-key',
  standalone: false,
  templateUrl: './board-admin-show-auth-key.component.html',
  styleUrl: './board-admin-show-auth-key.component.css'
})
export class BoardAdminShowAuthKeyComponent{
  form: any = {
    email: null,
  };

  constructor() {
  }
}
