package com.example.demo.repository;

import com.example.demo.entity.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsPost, Long> {
}
