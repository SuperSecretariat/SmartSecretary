from fastapi import FastAPI
from pydantic import BaseModel
from langchain_chroma import Chroma
from langchain_huggingface.embeddings import HuggingFaceEmbeddings
import os

app = FastAPI()

VECTOR_DB_DIR = "chroma_db"
EMBEDDING_MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"

embedding = HuggingFaceEmbeddings(model_name=EMBEDDING_MODEL_NAME)
vectordb = Chroma(persist_directory=VECTOR_DB_DIR, embedding_function=embedding)

retriever = vectordb.as_retriever(search_kwargs={"k": 10})

class PromptInput(BaseModel):
    prompt: str

@app.post("/rag")
def augment_prompt(input_data: PromptInput):
    if vectordb._collection.count() == 0:
        return {"error": "Vector DB este gol. Încarcă mai întâi niște PDF-uri."}

    try:
        retrieved_docs = retriever.get_relevant_documents(input_data.prompt)

        context_chunks = [doc.page_content for doc in retrieved_docs]
        context = "\n\n".join(context_chunks)

        final_prompt = f"""
                            Ești un secretar universitar bine informat, politicos și clar în explicații. Răspunde în limba română la întrebările studenților folosind informațiile din contextul oferit. Dacă întrebarea nu este acoperită de context, raspunde in baza cunostintelor tale precizand ca nu ai avut suficiente informatii in context si ai oferit informatii din cunostintele tale.
                            
                            Răspunsurile trebuie să fie:
                            - clare și directe
                            - politicoase și profesioniste
                            - adaptate unui student care nu cunoaște termenii birocratici
                            - formulate ca și cum ar fi oferite la un ghișeu de secretariat
                            
                            ### Context:
                            {context}
                            
                            ### Întrebare:
                            {input_data.prompt}
                        """

        return {"augmented_prompt": final_prompt.strip()}

    except Exception as e:
        return {"error": f"Eroare la procesarea promptului: {str(e)}"}
