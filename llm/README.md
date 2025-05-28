# Setup Instructions

To get started, follow these steps:

1. **Create a virtual environment** using `venv` with all the required Python libraries.
2. **Download the PDF files** you want to use.
3. **Run `create_embeddings.py`** to generate the chunks and store them in `chroma_db`.
4. **Run `rag_api.py`** to start the RAG-based API.

---

## More Details

You can find additional information and the full project repository here:  
🔗 [https://github.com/AndreiBalan98/ArtificialGeneralIntelligence](https://github.com/AndreiBalan98/ArtificialGeneralIntelligence)


venv, chroma_db, pdfuri out of the box, fara sa mai fie nevoie sa rulezi create_embeddings.py:
https://we.tl/t-GBGirgCU7Q (3 zile incepand din mai 28, ora 2:37)

pentru a rula noul serviciu transcription_api:
    pornit mediul virtual venv si rulat: pip install python-multipart openai-whisper
    descarcat FFmpeg, https://ffmpeg.org/download.html pentru windows, adaugat folderul ffmpeg/bin in path
                pentru linux sudo apt install ffmpeg

comenzi pentru a porni serviciile rag_api si transcript_api din venv:
    python -m uvicorn rag_api:app --reload --port 8000
    python -m uvicorn transcription_api:app --reload --port 8001