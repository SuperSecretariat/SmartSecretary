import { environment } from '../../../environments/environments';
import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './pubble-chat.component.html',
  styleUrls: ['./pubble-chat.component.css'],
  standalone: false,
})
export class PubbleChatComponent implements AfterViewInit {
  @ViewChild('chatBox') chatBox!: ElementRef<HTMLElement>;

  message: string = '';
  isLoading = false;

  // message list with sender and what's currently displayed
  messages: Array<{ sender: 'user' | 'bot'; text: string; displayed: string }> = [];

  ngAfterViewInit() {
    const inputField = document.getElementById('user-input') as HTMLInputElement;
    inputField?.focus();
  }

  async sendMessage() {
    const trimmed = this.message.trim();
    if (!trimmed) return;

    // push user message immediately
    this.messages.push({ sender: 'user', text: trimmed, displayed: trimmed });
    this.scrollToBottom();

    this.message = '';
    this.isLoading = true;

    let responseText = '';
    try {
      const provider = 'mistral';
      const resp = await fetch(`${environment.backendUrl}/api/pubble/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: trimmed, provider })
      });
      const data = await resp.json();
      responseText = data.answer;
    } catch (err) {
      responseText = 'Error when communicating with server';
    } finally {
      this.isLoading = false;
    }

    // add bot message placeholder
    const botMsg = { sender: 'bot' as const, text: responseText, displayed: '' };
    this.messages.push(botMsg);

    // type it out word by word
    const words = responseText.split(' ');
    let idx = 0;
    const interval = setInterval(() => {
      botMsg.displayed += (idx === 0 ? '' : ' ') + words[idx];
      this.scrollToBottom();
      idx++;
      if (idx >= words.length) {
        clearInterval(interval);
      }
    }, 50);
  }

  private scrollToBottom() {
    setTimeout(() => {
      const box = this.chatBox.nativeElement;
      box.scrollTop = box.scrollHeight;
    });
  }
}
