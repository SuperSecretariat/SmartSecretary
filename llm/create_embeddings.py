import os
import shutil
from langchain_community.document_loaders import PyPDFLoader
from langchain_core.documents import Document
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from langchain_huggingface.embeddings import HuggingFaceEmbeddings
from pdf2image import convert_from_path
import pytesseract
from PIL import Image

pytesseract.pytesseract.tesseract_cmd = r'/usr/bin/tesseract'

PDF_FOLDER = "pdfuri"
VECTOR_DB_DIR = "chroma_db"
EMBEDDING_MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"

def ocr_pdf_to_documents(path):
    print(f"!!! Performing OCR on {os.path.basename(path)}")
    pages = convert_from_path(path)
    documents = []
    for i, page in enumerate(pages):
        text = pytesseract.image_to_string(page)
        if text.strip():
            documents.append(Document(page_content=text, metadata={"source": path, "page": i + 1}))
    return documents

if os.path.exists(VECTOR_DB_DIR):
    print("!!! Removing old vector database...")
    shutil.rmtree(VECTOR_DB_DIR)

all_docs = []

for root, _, files in os.walk(PDF_FOLDER):
    for file in files:
        if file.lower().endswith(".pdf"):
            path = os.path.join(root, file)
            print(f"!!! Loading {os.path.relpath(path, PDF_FOLDER)}")
            loader = PyPDFLoader(path)
            docs = loader.load()

            if all(not d.page_content.strip() for d in docs):
                docs = ocr_pdf_to_documents(path)

            if docs:
                all_docs.extend(docs)

if not all_docs:
    print("!!! No PDF documents loaded. Check the folder or files.")
    exit()

print(f"!!! Loaded {len(all_docs)} pages from PDFs (including OCR if needed).")

splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=300)
chunks = splitter.split_documents(all_docs)
print(f"!!! Split into {len(chunks)} chunks.")

embedding = HuggingFaceEmbeddings(model_name=EMBEDDING_MODEL_NAME)
vectordb = Chroma.from_documents(chunks, embedding, persist_directory=VECTOR_DB_DIR)

print(f"!!! Vector DB saved to: {VECTOR_DB_DIR}")
