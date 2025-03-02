package com.example.aniwhere.repository.anime.repository;

import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO;
import com.example.aniwhere.domain.anime.dto.AnimeSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AnimeCustomRepository {

    List<AnimeDTO.AnimeGroupedByWeekdayDTO> findAllGroupedByWeekday(Integer year, Integer quarter);

//    PageResponse<AnimeSearchResponseDto> findByFilters(List<String> categories, List<Integer> quarters, String title, List<String> statuses, PageRequest pageRequest);

    PageResponse<AnimeSearchResponseDto> findByFilters(
            List<String> categories, List<Integer> quarters, String title, List<String> statuses, Integer year, PageRequest pageRequest);
}
