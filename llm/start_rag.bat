@echo off
setlocal

echo ================================
echo Activare mediu virtual...
call venv\Scripts\activate.bat

if errorlevel 1 (
    echo ❌ Eroare la activarea mediului virtual.
    pause
    exit /b 1
)

echo ================================
echo Pornire server FastAPI (RAG)...
echo URL: http://localhost:8000
echo ================================

python -m uvicorn rag_api:app --reload --port 8000

echo ================================
echo Serverul s-a oprit sau a aparut o eroare.
pause
