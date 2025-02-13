package com.example.aniwhere.repository.animeReview;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewResponse;

public interface AnimeReviewRepositoryCustom {
    PageResponse<AnimeReviewResponse> getAnimeReviews(Long animeId, PageRequest request);
}
