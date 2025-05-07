package com.example.demo.controllers;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;


import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /** 1️⃣ Show the upload form */
    @GetMapping("/")
    public String showUploadForm() {
        return "index";
    }

    /** 2️⃣ Handle file uploads (you already have this) */
    @PostMapping("/addDocument")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file.");
            return "index";
        }
        try {
            Document doc = new Document();
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(file.getContentType());
            doc.setData(file.getBytes());
            documentService.save(doc);
            return "redirect:/v1/documents/list";
        } catch (IOException e) {
            model.addAttribute("error", "Error reading file: " + e.getMessage());
            return "index";
        }
    }

    /** 3️⃣ Display a list of all uploaded documents */
    @GetMapping("/list")
    public String listDocuments(Model model) {
        List<Document> docs = documentService.findAll();
        model.addAttribute("documents", docs);
        return "list";
    }

    /** 4️⃣ Download a document by ID */
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Integer id) {
        Document doc = documentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Document not found: " + id));

        ByteArrayResource resource = new ByteArrayResource(doc.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}
