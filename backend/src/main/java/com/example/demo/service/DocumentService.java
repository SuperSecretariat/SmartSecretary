package com.example.demo.service;

import com.example.demo.entity.Document;
import org.springframework.stereotype.Service;
import com.example.demo.repository.DocumentRepository;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Document save(Document document) {
        return documentRepository.save(document);
    }
}