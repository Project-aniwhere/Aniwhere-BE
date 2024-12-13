package com.example.aniwhere.controller.episodes;

import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.global.common.ApiResponse;
import com.example.aniwhere.service.episodes.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Episode", description = "에피소드 관련 API")
public class EpisodeApiController {

	private final EpisodeService episodesService;

	@Operation(
			summary = "애니메이션 ID값에 대한 전체 에피소드 조회",
			description = "특정 애니메이션의 모든 에피소드를 페이지 단위로 조회합니다."
	)
	@GetMapping("/episodes/{animeId}")
	public Page<EpisodesDto> getEpisodes(@PathVariable(name = "animeId") Long animeId, Pageable pageable) {
		return episodesService.getEpisodes(animeId, pageable);
	}

	@Operation(
			summary = "에피소드에 대한 리뷰 작성",
			description = "에피소드별 리뷰를 작성할 수 있습니다."
	)
	@PostMapping("/episodes/{episodeId}/reviews")
	public ResponseEntity<ApiResponse> addReview(@PathVariable(name = "episodeId") Long episodeId, @RequestBody EpisodeReviewRequest request) {
		episodesService.addReview(episodeId, request);
        return ResponseEntity.status(201).body(ApiResponse.of(201, "리뷰 등록 성공"));
	}
}