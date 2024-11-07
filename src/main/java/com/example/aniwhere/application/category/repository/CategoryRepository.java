package com.example.aniwhere.application.category.repository;

import com.example.aniwhere.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
