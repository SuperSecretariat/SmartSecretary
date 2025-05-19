import { Component, AfterViewInit } from '@angular/core';
import { environment } from '../../../environments/environments';

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

    chatBox.innerHTML += `<div class="message user"><strong>Tu:</strong> ${trimmed}</div>`;
    this.message = '';
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
      const response = await fetch(`${environment.backendUrl}/api/pubble/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: trimmed, provider })
      });

      const data = await response.json();

      chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> ${data.answer}</div>`;

      // if (data.chunks?.length) {
      //   chatBox.innerHTML += `<div class="message bot"><strong>Surse:</strong></div>`;
      //   data.chunks.forEach((chunk: string, index: number) => {
      //     chatBox.innerHTML += `<div class="message bot">(${index + 1}) ${chunk}</div>`;
      //   });
      // }

      // if (data.sources?.length) {
      //   chatBox.innerHTML += `<div class="message bot"><strong>Chunks:</strong></div>`;
      //   data.sources.forEach((source: string, index: number) => {
      //     chatBox.innerHTML += `<div class="message bot">(${index + 1}) ${source}</div>`;
      //   });
      // }

      chatBox.scrollTop = chatBox.scrollHeight;
    } catch (err) {
      chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> Eroare la comunicare cu serverul.</div>`;
    }
  }
}
