import { Component, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './pubble-chat.component.html',
  styleUrls: ['./pubble-chat.component.css'],
  standalone: false,
})
export class PubbleChatComponent implements AfterViewInit {
  message: string = '';

  ngAfterViewInit() {
    const inputField = document.getElementById('user-input') as HTMLInputElement;
    inputField?.focus();
  }

  async sendMessage() {
    const trimmed = this.message.trim();
    if (!trimmed) return;

    const provider = 'mistral';
    const chatBox = document.getElementById('chat-box') as HTMLElement;

    chatBox.innerHTML += `<div id="message-user" class="message-user"><strong>You:</strong> ${trimmed}</div>`;
    this.message = '';
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
      const response = await fetch('http://localhost:8081/api/pubble/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: trimmed, provider })
      });

      const data = await response.json();

      chatBox.innerHTML += `<div class="message-bot"><strong>Model:</strong> ${data.answer}</div>`;

      chatBox.scrollTop = chatBox.scrollHeight;
    } catch (err) {
      chatBox.innerHTML += `<div class="message-bot"><strong>Model:</strong> Error when communicating with server</div>`;
    }
  }
}
