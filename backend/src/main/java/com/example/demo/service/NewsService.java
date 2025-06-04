package com.example.demo.service;

import com.example.demo.entity.NewsPost;
import com.example.demo.repository.NewsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    private NewsRepository newsRepository;
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsPost updateNewsWithFile(Long id, String title, String body, Boolean hidden, MultipartFile file) throws IOException {
        NewsPost news = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id " + id));
        news.setTitle(title);
        news.setBody(body);
        news.setHidden(hidden);

        if (file != null && !file.isEmpty()) {
            news.setFileName(file.getOriginalFilename());
            news.setFileType(file.getContentType());
            news.setFileData(file.getBytes());
        }
        return newsRepository.save(news);
    }

    public List<NewsPost> getAllNews() {
        return newsRepository.findAll();
    }

    public Optional<NewsPost> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    public NewsPost saveNews(NewsPost news) {
        return newsRepository.save(news);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
