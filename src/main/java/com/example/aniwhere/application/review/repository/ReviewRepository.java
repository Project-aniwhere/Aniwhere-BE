package com.example.aniwhere.application.review.repository;

import com.example.aniwhere.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAnimeId(Long animeId);
}
