package com.example.aniwhere.service.tag;

import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeSearchResponseDto;
import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.domain.category.dto.CategoryDTO;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final CategoryRepository categoryRepository;
    private final AnimeRepository animeRepository;


    /**
     * 카테고리 목록을 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO.CategoryResponseDTO> getCategoriesList() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO.CategoryResponseDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * 태그 기반 Anime 검색
     */
    @Transactional(readOnly = true)
    public PageResponse<AnimeSearchResponseDto> searchAnimeByTag(
            List<String> categories, List<Integer> quarters, String title, List<String> statuses, Integer year, PageRequest pageRequest) {

        log.info("QueryDSL 태그 검색 요청 - 카테고리: {}, 분기: {}, 제목: {}, 상태: {}", categories, quarters, title, statuses);

        return animeRepository.findByFilters(categories, quarters, title, statuses, year, pageRequest);
    }

}
