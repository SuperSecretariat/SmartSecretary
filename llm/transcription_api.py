from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
import whisper
import tempfile
import os
import logging
import subprocess
import shutil

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

def check_ffmpeg():
    """Verifică dacă FFmpeg este disponibil"""
    try:
        result = subprocess.run(['ffmpeg', '-version'],
                              capture_output=True, text=True, timeout=10)
        if result.returncode == 0:
            logger.info("FFmpeg is available")
            return True
    except (subprocess.TimeoutExpired, FileNotFoundError, subprocess.SubprocessError):
        pass

    # Verifică dacă ffmpeg este în PATH
    if shutil.which('ffmpeg'):
        logger.info("FFmpeg found in PATH")
        return True

    logger.error("FFmpeg not found. Please install FFmpeg to use audio transcription.")
    return False

# Verifică FFmpeg la startup
ffmpeg_available = check_ffmpeg()

if ffmpeg_available:
    logger.info("Loading Whisper model...")
    try:
        model = whisper.load_model("base")
        logger.info("Whisper model loaded successfully!")
        model_loaded = True
    except Exception as e:
        logger.error(f"Failed to load Whisper model: {e}")
        model_loaded = False
else:
    logger.error("Cannot load Whisper model without FFmpeg")
    model_loaded = False

@app.post("/transcribe")
async def transcribe_audio(audio: UploadFile = File(...)):
    """
    Transcribe audio file to text using Whisper
    """
    if not ffmpeg_available:
        return JSONResponse(
            status_code=500,
            content={
                "error": "FFmpeg is not installed. Please install FFmpeg to use audio transcription.",
                "status": "error",
                "help": "Install FFmpeg: https://ffmpeg.org/download.html"
            }
        )

    if not model_loaded:
        return JSONResponse(
            status_code=500,
            content={
                "error": "Whisper model is not loaded",
                "status": "error"
            }
        )

    try:
        logger.info(f"Received file type: {audio.content_type}")
        logger.info(f"File name: {audio.filename}")

        # Lista de tipuri de fișiere acceptate
        accepted_types = [
            'audio/',
            'video/webm',
            'video/mp4',
            'application/octet-stream'
        ]

        if audio.content_type:
            is_accepted = any(audio.content_type.startswith(accepted_type) for accepted_type in accepted_types)
        else:
            is_accepted = True

        if not is_accepted:
            logger.warning(f"Rejected file type: {audio.content_type}")
            return JSONResponse(
                status_code=400,
                content={
                    "error": f"Unsupported file type: {audio.content_type}",
                    "status": "error"
                }
            )

        # Determină extensia fișierului
        if audio.filename and '.' in audio.filename:
            file_extension = audio.filename.split('.')[-1].lower()
        elif audio.content_type == 'video/webm':
            file_extension = 'webm'
        else:
            file_extension = 'wav'

        # Creează fișier temporar
        with tempfile.NamedTemporaryFile(delete=False, suffix=f".{file_extension}") as tmp_file:
            content = await audio.read()
            tmp_file.write(content)
            tmp_file_path = tmp_file.name

        logger.info(f"Processing audio file: {audio.filename} (saved as {tmp_file_path})")
        logger.info(f"File size: {len(content)} bytes")

        # Verifică dacă fișierul nu este gol
        if len(content) == 0:
            os.unlink(tmp_file_path)
            return JSONResponse(
                status_code=400,
                content={
                    "error": "Audio file is empty",
                    "status": "error"
                }
            )

        # Transcrie audio
        try:
            result = model.transcribe(tmp_file_path, language="ro")
            transcribed_text = result["text"].strip()

            # Log the transcription result
            logger.info(f"Raw transcription result: '{transcribed_text}'")

        except Exception as transcription_error:
            logger.error(f"Whisper transcription error: {str(transcription_error)}")
            os.unlink(tmp_file_path)
            return JSONResponse(
                status_code=500,
                content={
                    "error": f"Transcription failed: {str(transcription_error)}",
                    "status": "error"
                }
            )

        # Șterge fișierul temporar
        os.unlink(tmp_file_path)

        # Handle empty or very short transcriptions (likely no speech)
        if not transcribed_text or len(transcribed_text.strip()) < 2:
            logger.info("No meaningful speech detected in audio")
            return JSONResponse(content={
                "text": "",
                "status": "success",
                "message": "No speech detected in audio"
            })

        logger.info(f"Transcription completed successfully: {transcribed_text[:50]}...")

        return JSONResponse(content={
            "text": transcribed_text,
            "status": "success"
        })

    except Exception as e:
        logger.error(f"Transcription error: {str(e)}")
        if 'tmp_file_path' in locals() and os.path.exists(tmp_file_path):
            os.unlink(tmp_file_path)

        return JSONResponse(
            status_code=500,
            content={
                "error": f"Transcription failed: {str(e)}",
                "status": "error"
            }
        )

@app.get("/health")
def health_check():
    """Health check endpoint"""
    status = {
        "status": "healthy" if (ffmpeg_available and model_loaded) else "unhealthy",
        "ffmpeg_available": ffmpeg_available,
        "model_loaded": model_loaded,
        "model": "whisper-base" if model_loaded else None
    }

    if not ffmpeg_available:
        status["error"] = "FFmpeg not available"
    if not model_loaded:
        status["error"] = "Whisper model not loaded"

    return status

@app.get("/")
def root():
    """Root endpoint with service info"""
    return {
        "service": "Audio Transcription API",
        "version": "1.0.0",
        "endpoints": {
            "/transcribe": "POST - Upload audio file for transcription",
            "/health": "GET - Health check",
        },
        "requirements": {
            "ffmpeg": "Required for audio processing",
            "whisper": "AI model for speech recognition"
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)