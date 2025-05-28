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
    const inputField = document.getElementById('user-input') as HTMLInputElement;
    inputField?.focus();
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
    console.log('Toggle recording called, current state:', this.isRecording);
    console.log('MediaDevices support:', !!navigator.mediaDevices);
    console.log('getUserMedia support:', !!navigator.mediaDevices?.getUserMedia);

    if (!this.isRecording) {
      await this.startRecording();
    } else {
      await this.stopRecording();
    }
  }

  private async startRecording() {
    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        alert('Your browser does not support audio recording');
        return;
      }

      const stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true
        }
      });

      const options: MediaRecorderOptions = {};

      if (MediaRecorder.isTypeSupported('audio/webm;codecs=opus')) {
        options.mimeType = 'audio/webm;codecs=opus';
      } else if (MediaRecorder.isTypeSupported('audio/webm')) {
        options.mimeType = 'audio/webm';
      } else if (MediaRecorder.isTypeSupported('audio/mp4')) {
        options.mimeType = 'audio/mp4';
      }

      this.mediaRecorder = new MediaRecorder(stream, options);
      console.log('MediaRecorder created with mimeType:', this.mediaRecorder.mimeType);
      this.audioChunks = [];

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = async () => {
        const mimeType = this.mediaRecorder?.mimeType || 'audio/webm';
        const audioBlob = new Blob(this.audioChunks, { type: mimeType });
        console.log('Audio blob created:', audioBlob.type, 'size:', audioBlob.size);

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

    } catch (error) {
      console.error('Error accessing microphone:', error);

      if (error.name === 'NotAllowedError') {
        alert('Microphone access denied. Please allow microphone permissions and try again.');
      } else if (error.name === 'NotFoundError') {
        alert('No microphone found on this device.');
      } else if (error.name === 'NotSupportedError') {
        alert('Audio recording is not supported on this device/browser.');
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

      if (result.status === 'success') {
        if (result.text && result.text.trim()) {
          this.message = result.text.trim();
          setTimeout(() => {
            const inputField = document.getElementById('user-input') as HTMLInputElement;
            inputField?.focus();
            this.cdr.detectChanges();
          }, 100);
        } else {
          console.log('No speech detected in audio recording');
        }
      } else {
        if (result.error && !result.error.includes('No speech detected')) {
          console.error('Transcription failed:', result.error);
          alert('Transcription failed. Please try again.');
        } else {
          console.log('No speech detected in audio recording');
        }
      }

    } catch (error) {
      console.error('Error during transcription:', error);
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
