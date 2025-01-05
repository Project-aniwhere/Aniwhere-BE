package com.example.aniwhere.service.tag;

import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.domain.category.dto.CategoryDTO;
import com.example.aniwhere.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO.CategoryResponseDTO> getCategoriesList() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO.CategoryResponseDTO::of)
                .collect(Collectors.toList());
    }
}
