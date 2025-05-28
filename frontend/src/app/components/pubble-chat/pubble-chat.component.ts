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
  isRecording = false;
  isTranscribing = false;

  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];

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
      const provider = 'microsoft';
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

  async toggleRecording() {
    if (!this.isRecording) {
      await this.startRecording();
    } else {
      await this.stopRecording();
    }
  }

  private async startRecording() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

      this.mediaRecorder = new MediaRecorder(stream);
      this.audioChunks = [];

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
              this.audioChunks.push(event.data);
            }
          };

          this.mediaRecorder.onstop = async () => {
            const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
            console.log('Sending audio blob:', audioBlob.type);

            await this.transcribeAudio(audioBlob);

            stream.getTracks().forEach(track => track.stop());
          };

          this.mediaRecorder.start();
          this.isRecording = true;

        } catch (error) {
          console.error('Error accessing microphone:', error);
          alert('Error accessing microphone. Please check permissions.');
        }
      }

    private async stopRecording() {
      if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
        this.mediaRecorder.stop();
        this.isRecording = false;
      }
    }

    private async transcribeAudio(audioBlob: Blob) {
      this.isTranscribing = true;

      try {
        const formData = new FormData();
        formData.append('audio', audioBlob, 'recording.webm');

        const response = await fetch(`${environment.backendUrl}/api/audio/transcribe`, {
          method: 'POST',
          body: formData
        });

        const result = await response.json();

        if (result.text) {
          this.message = result.text;
          // Focus input field so user can edit if needed
          setTimeout(() => {
            const inputField = document.getElementById('user-input') as HTMLInputElement;
            inputField?.focus();
          }, 100);
        } else {
          console.error('Transcription failed:', result.error);
          alert('Transcription failed. Please try again.');
        }

      } catch (error) {
        console.error('Error during transcription:', error);
        alert('Error during transcription. Please try again.');
      } finally {
        this.isTranscribing = false;
      }
    }

  private scrollToBottom() {
    setTimeout(() => {
      const box = this.chatBox.nativeElement;
      box.scrollTop = box.scrollHeight;
    });
  }
}
