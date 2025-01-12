package com.example.aniwhere.domain.category.dto;

import com.example.aniwhere.domain.category.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CategoryDTO {
    @Getter
    @Setter
    public static class CategoryRequestDTO{
        private String categoryName;
    }

    @Getter
    @Setter
    @Builder
    public static class CategoryResponseDTO{
        private Long categoryId;
        private String categoryName;

        public static CategoryResponseDTO of(Category category){
            return CategoryResponseDTO.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getCategoryName())
                    .build();
        }
    }
}
