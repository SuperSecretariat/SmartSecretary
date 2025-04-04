package com.example.demo.service;

import com.example.demo.model.Document;
import org.springframework.stereotype.Service;
import com.example.demo.repository.DocumentRepository;

import java.util.List;

@Service
public class DocumentService {
    private DocumentRepository documentRepository;
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    public List<Document> findAll() {
        return documentRepository.findAll();
    }
    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }
    public void save(Document document) {
        documentRepository.save(document);
    }
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }
}
