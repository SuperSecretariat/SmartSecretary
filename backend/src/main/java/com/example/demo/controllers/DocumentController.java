package com.example.demo.controllers;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/")
    public String index(Model model) {
        return "index"; // This will render the index.html page
    }

    @GetMapping("/documents")
    @ResponseBody
    public List<Document> getDocuments() {
        return documentService.findAll(); // Fetch and return all documents
    }

}