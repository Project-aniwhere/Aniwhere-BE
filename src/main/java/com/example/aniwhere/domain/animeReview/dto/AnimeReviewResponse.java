package com.example.aniwhere.domain.animeReview.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AnimeReviewResponse(Long animeId, Double rating, String content, String nickname) {
    @QueryProjection
    public AnimeReviewResponse {
    }
}
