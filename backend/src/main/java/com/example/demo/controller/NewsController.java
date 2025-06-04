package com.example.demo.controller;

import com.example.demo.entity.NewsPost;
import com.example.demo.service.NewsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private NewsService newsService;
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsPost> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsPost> getNewsById(@PathVariable Long id) {
        Optional<NewsPost> news = newsService.getNewsById(id);
        return news.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<NewsPost> createNews(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam("hidden") Boolean hidden,
            @RequestPart(required = false) MultipartFile file) throws IOException {

        NewsPost news = new NewsPost();
        news.setTitle(title);
        news.setBody(body);
        news.setHidden(hidden);

        if (file != null && !file.isEmpty()) {
            news.setFileName(file.getOriginalFilename());
            news.setFileType(file.getContentType());
            news.setFileData(file.getBytes());
        }

        NewsPost saved = newsService.saveNews(news);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNewsWithFile(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam("hidden") Boolean hidden,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            NewsPost updated = newsService.updateNewsWithFile(id, title, body, hidden, file);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not update news: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Optional<NewsPost> newsOpt = newsService.getNewsById(id);
        if (newsOpt.isEmpty() || newsOpt.get().getFileData() == null) {
            return ResponseEntity.notFound().build();
        }
        NewsPost news = newsOpt.get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(news.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + news.getFileName() + "\"")
                .body(news.getFileData());
    }
}