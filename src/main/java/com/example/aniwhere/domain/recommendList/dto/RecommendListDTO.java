package com.example.aniwhere.domain.recommendList.dto;

import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import lombok.Builder;

import java.util.List;

@Builder
public class RecommendListDTO {
    private Long id;
    private String title;
    private String description;
    private List<AnimeSummaryDTO> animes;
}