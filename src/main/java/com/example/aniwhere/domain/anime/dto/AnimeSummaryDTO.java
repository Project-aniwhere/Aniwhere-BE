package com.example.aniwhere.domain.anime.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnimeSummaryDTO {
    private Long animeId;         // 애니메이션 ID
    private String title;         // 애니메이션 제목
    private String poster;        // 포스터 URL
    private double averageRating; // 평균 평점
}
