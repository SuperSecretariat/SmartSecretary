import { environment } from '../../../environments/environments';
import { Component, AfterViewInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';

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

  messages: Array<{ sender: 'user' | 'bot'; text: string; displayed: string }> = [];

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit() {
    (document.getElementById('user-input') as HTMLInputElement)?.focus();
  }

  async sendMessage() {
    const trimmed = this.message.trim();
    if (!trimmed) return;

    this.messages.push({ sender: 'user', text: trimmed, displayed: trimmed });
    this.scrollToBottom();
    this.message = '';
    this.isLoading = true;

    let responseText = '';
    try {
      const resp = await fetch(`${environment.backendUrl}/api/pubble/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: trimmed, provider: 'mistral' })
      });
      const data = await resp.json();
      responseText = data.answer;
    } catch {
      responseText = 'Error when communicating with server';
    } finally {
      this.isLoading = false;
    }

    const botMsg = { sender: 'bot' as const, text: responseText, displayed: '' };
    this.messages.push(botMsg);

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
      if (!navigator.mediaDevices?.getUserMedia) {
        alert('Your browser does not support audio recording');
        return;
      }

      const stream = await navigator.mediaDevices.getUserMedia({
        audio: { echoCancellation: true, noiseSuppression: true, autoGainControl: true }
      });

      const options: MediaRecorderOptions = {};
      if (MediaRecorder.isTypeSupported('audio/webm;codecs=opus')) {
        options.mimeType = 'audio/webm;codecs=opus';
      } else if (MediaRecorder.isTypeSupported('audio/webm')) {
        options.mimeType = 'audio/webm';
      }

      this.mediaRecorder = new MediaRecorder(stream, options);
      this.audioChunks = [];

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = async () => {
        const audioBlob = new Blob(this.audioChunks, {
          type: this.mediaRecorder?.mimeType || 'audio/webm'
        });

        this.isTranscribing = true;
        this.cdr.detectChanges();

        try {
          await this.transcribeAudio(audioBlob);
        } finally {
          this.isTranscribing = false;
          this.cdr.detectChanges();
        }

        stream.getTracks().forEach(track => track.stop());
      };

      this.mediaRecorder.start();
      this.isRecording = true;

    } catch (error: any) {
      if (error.name === 'NotAllowedError') {
        alert('Microphone access denied. Please allow microphone permissions and try again.');
      } else if (error.name === 'NotFoundError') {
        alert('No microphone found on this device.');
      } else {
        alert(`Error accessing microphone: ${error.message || 'Unknown error'}`);
      }
    }
  }

  private async stopRecording() {
    if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
      this.mediaRecorder.stop();
      this.isRecording = false;
    }
  }

  private async transcribeAudio(audioBlob: Blob) {
    try {
      const formData = new FormData();
      formData.append('audio', audioBlob, 'recording.webm');

      const response = await fetch(`${environment.backendUrl}/api/audio/transcribe`, {
        method: 'POST',
        body: formData
      });

      const result = await response.json();

      if (result.status === 'success' && result.text?.trim()) {
        this.message = result.text.trim();
        setTimeout(() => {
          (document.getElementById('user-input') as HTMLInputElement)?.focus();
          this.cdr.detectChanges();
        }, 100);
      }
    } catch (error) {
      alert('Error during transcription. Please try again.');
    }
  }

  private scrollToBottom() {
    setTimeout(() => {
      const box = this.chatBox.nativeElement;
      box.scrollTop = box.scrollHeight;
    });
  }
}
