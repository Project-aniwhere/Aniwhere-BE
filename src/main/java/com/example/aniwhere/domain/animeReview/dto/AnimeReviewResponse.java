package com.example.aniwhere.domain.animeReview.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AnimeReviewResponse(Long id, Long animeId, Double rating, String content, String nickname, Long userId) {
    @QueryProjection
    public AnimeReviewResponse {
    }
}