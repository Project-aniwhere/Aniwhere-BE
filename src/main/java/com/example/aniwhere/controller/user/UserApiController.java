package com.example.aniwhere.controller.user;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import com.example.aniwhere.repository.episodesReview.EpisodesReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "사용자 관련 API")
@RequiredArgsConstructor
public class UserApiController {

	private final EpisodesReviewRepository episodesReviewRepository;

	@Operation(
			summary = "내가 작성한 에피소드 리뷰 목록",
			description = "자신이 작성한 에피소드 리뷰 목록을 조회합니다."
	)
	@GetMapping("/users/me/episode-reviews")
	public ResponseEntity<Page<EpisodeReviewResponse>> getUserEpisodeReviews(@LoginUser Long userId, Pageable pageable) {
		Page<EpisodeReviewResponse> reviews = episodesReviewRepository.getMyEpisodeReviews(userId, pageable);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(reviews);
	}

	@Operation(
			summary = "특정 사용자가 작성한 에피소드 리뷰 목록",
			description = "특정 사용자가 작성한 에피소드 리뷰 목록을 조회합니다."
	)
	@GetMapping("/users/{nickName}/episode-reviews")
	public ResponseEntity<Page<EpisodeReviewResponse>> getUserEpisodeReviews(@PathVariable(name = "nickName") String nickName, Pageable pageable) {
		Page<EpisodeReviewResponse> reviews = episodesReviewRepository.getUserEpisodeReviews(nickName, pageable);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(reviews);
	}
}