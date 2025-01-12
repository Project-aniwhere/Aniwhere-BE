package com.example.aniwhere.repository.category.repository;

import com.example.aniwhere.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.categoryName FROM Category c")
    List<String> findAllCategoryNames();
}
