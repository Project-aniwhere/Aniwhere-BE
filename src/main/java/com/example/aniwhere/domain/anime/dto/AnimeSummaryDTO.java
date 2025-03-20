package com.example.aniwhere.domain.anime.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnimeSummaryDTO {
    private Long animeId;
    private String title;
    private String description;
    private String poster;
    private String studio;
    private int episodes;
    private double averageRating;
    private String latestReview;
}
