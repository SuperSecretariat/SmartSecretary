from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
import whisper
import tempfile
import os
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

try:
    model = whisper.load_model("base")
    model_loaded = True
    logger.info("Whisper model loaded successfully")
except Exception as e:
    model_loaded = False
    logger.error(f"Failed to load Whisper model: {e}")

@app.post("/transcribe")
async def transcribe_audio(audio: UploadFile = File(...)):
    if not model_loaded:
        return JSONResponse(
            status_code=500,
            content={"error": "Whisper model is not loaded", "status": "error"}
        )

    try:
        content = await audio.read()

        if len(content) == 0:
            return JSONResponse(
                status_code=400,
                content={"error": "Audio file is empty", "status": "error"}
            )

        file_extension = "webm"
        if audio.filename and '.' in audio.filename:
            file_extension = audio.filename.split('.')[-1].lower()

        with tempfile.NamedTemporaryFile(delete=False, suffix=f".{file_extension}") as tmp_file:
            tmp_file.write(content)
            tmp_file_path = tmp_file.name

        try:
            result = model.transcribe(tmp_file_path, language="ro")
            transcribed_text = result["text"].strip()
        except Exception as e:
            os.unlink(tmp_file_path)
            return JSONResponse(
                status_code=500,
                content={"error": f"Transcription failed: {str(e)}", "status": "error"}
            )

        os.unlink(tmp_file_path)

        if not transcribed_text or len(transcribed_text.strip()) < 2:
            return JSONResponse(content={
                "text": "",
                "status": "success",
                "message": "No speech detected in audio"
            })

        return JSONResponse(content={
            "text": transcribed_text,
            "status": "success"
        })

    except Exception as e:
        if 'tmp_file_path' in locals() and os.path.exists(tmp_file_path):
            os.unlink(tmp_file_path)

        return JSONResponse(
            status_code=500,
            content={"error": f"Transcription failed: {str(e)}", "status": "error"}
        )

@app.get("/health")
def health_check():
    return {
        "status": "healthy" if model_loaded else "unhealthy",
        "model_loaded": model_loaded,
        "model": "whisper-base" if model_loaded else None
    }

@app.get("/")
def root():
    return {
        "service": "Audio Transcription API",
        "version": "1.0.0",
        "endpoints": {
            "/transcribe": "POST - Upload audio file for transcription",
            "/health": "GET - Health check",
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)