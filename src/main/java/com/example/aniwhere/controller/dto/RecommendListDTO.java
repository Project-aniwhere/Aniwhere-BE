package com.example.aniwhere.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class RecommendListDTO {
    private Long id;
    private String title;
    private String description;
    private List<AnimeSummaryDTO> animes;
}