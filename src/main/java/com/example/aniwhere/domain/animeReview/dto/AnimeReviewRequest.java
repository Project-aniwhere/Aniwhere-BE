package com.example.aniwhere.domain.animeReview.dto;

import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public class AnimeReviewRequest {
    @Range(min = 0, max = 5, message = "평점은 0~5 사이로 매길 수 있습니다.") double rating;
}
