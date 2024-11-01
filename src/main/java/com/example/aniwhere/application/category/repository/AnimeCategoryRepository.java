package com.example.aniwhere.application.category.repository;

import com.example.aniwhere.domain.animecategory.AnimeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimeCategoryRepository extends JpaRepository<AnimeCategory, Long> {
    List<AnimeCategory> findByAnimeId(Long animeId);

    List<AnimeCategory> findByCategoryId(Long categoryId);
}
