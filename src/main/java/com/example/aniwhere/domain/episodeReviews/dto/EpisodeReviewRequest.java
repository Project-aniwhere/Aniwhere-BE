package com.example.aniwhere.domain.episodeReviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EpisodeReviewRequest {

	@Min(value = 0, message = "별점은 0이상이어야 합니다.")
	@Max(value = 5, message = "별점은 5이하여야 합니다.")
	private Double rating;

	private String content;
	private Long userId;
}
