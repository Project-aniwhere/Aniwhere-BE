package com.example.aniwhere.domain.anime.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class AnimeSummaryDTO {
        private Long animeId;
        private String title;
        private String poster;
}
