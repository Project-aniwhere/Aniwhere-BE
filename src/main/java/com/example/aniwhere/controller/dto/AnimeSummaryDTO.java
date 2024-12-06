package com.example.aniwhere.controller.dto;

import lombok.Builder;

@Builder
public class AnimeSummaryDTO {
        private Long animeId;
        private String title;
        private String poster;
}
