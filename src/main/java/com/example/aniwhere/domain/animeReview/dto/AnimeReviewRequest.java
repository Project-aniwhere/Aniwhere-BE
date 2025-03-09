package com.example.aniwhere.domain.animeReview.dto;

import org.hibernate.validator.constraints.Range;

public record AnimeReviewRequest(
        @Range(min = 0, max = 5, message = "평점은 0~5 사이로 매길 수 있습니다.")
        double rating, String content) {

    public static AnimeReviewRequest of(final double rating, final String content) {
        return new AnimeReviewRequest(rating, content);
    }
}