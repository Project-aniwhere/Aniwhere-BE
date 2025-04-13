package com.example.aniwhere.domain.anime.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import static com.example.aniwhere.domain.category.dto.CategoryDTO.*;

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
    private LocalDate releaseDate;
    private double averageRating;
    private String latestReview;
    private List<CategoryResponseDTO> categories;
}