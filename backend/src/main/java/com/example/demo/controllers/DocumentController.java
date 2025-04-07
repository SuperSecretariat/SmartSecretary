package com.example.demo.controllers;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/addDocument")
    public String addDocument(@RequestParam("name") String name, @RequestParam("type") String type, Model model) {
        try {
            Document document = new Document();
            document.setName(name);
            document.setType(type);
            documentService.save(document); // Save the new document to the database
            return "redirect:/"; // Redirect to the index page after submission
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while adding the document: " + e.getMessage());
            return "index"; // Return to the index page with an error message
        }
    }

}