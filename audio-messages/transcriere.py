import sys
import os
import whisper

def transcribe_audio(file_path):
    model = whisper.load_model("small")
    result = model.transcribe(file_path, language="ro")
    return result["text"]

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python transcriere.py <cale_fisier_audio>")
        sys.exit(1)
    audio_file = sys.argv[1]
    text = transcribe_audio(audio_file)
    sys.stdout.reconfigure(encoding='utf-8')  
    print(text)
    txt_file = os.path.splitext(audio_file)[0] + ".txt"
    with open(txt_file, "w", encoding="utf-8") as f:
        f.write(text)