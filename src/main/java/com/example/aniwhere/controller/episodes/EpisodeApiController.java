package com.example.aniwhere.controller.episodes;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.EpisodesInfoDto;
import com.example.aniwhere.repository.episodes.EpisodesRepository;
import com.example.aniwhere.repository.episodesReview.EpisodesReviewRepository;
import com.example.aniwhere.service.episodes.dto.EpisodeReviewCommand;
import com.example.aniwhere.service.episodes.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Episode", description = "에피소드 관련 API")
public class EpisodeApiController {

	private final EpisodeService episodesService;
	private final EpisodesRepository episodesRepository;
	private final EpisodesReviewRepository episodeReviewRepository;

	@Operation(
			summary = "애니메이션 ID값에 대한 전체 에피소드 조회",
			description = "특정 애니메이션의 모든 에피소드를 페이지 단위로 조회합니다."
	)
	@GetMapping("/anime/{animeId}/episodes")
	public ResponseEntity<PageResponse<EpisodesDto>> getEpisodes(@PathVariable(name = "animeId") Long animeId, PageRequest request) {

		PageResponse<EpisodesDto> episodes = episodesRepository.getEpisodes(animeId, request);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(episodes);
	}

	@Operation(
			summary = "에피소드 상세 조회",
			description = "에피소드 상세 정보들을 조회합니다."
	)
	@GetMapping("/episodes/{episodeId}")
    public ResponseEntity<List<EpisodesInfoDto>> getEpisode(@PathVariable(name = "episodeId") Long episodeId) {

		List<EpisodesInfoDto> episodes = episodesRepository.getEpisodeById(episodeId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(episodes);
	}

	@Operation(
			summary = "에피소드에 대한 리뷰 작성",
			description = "에피소드별 리뷰를 작성할 수 있습니다."
	)
	@PostMapping("/episodes/{episodeId}/reviews")
	public ResponseEntity<Void> addReview(@PathVariable(name = "episodeId") final Long episodeId,
										  @Valid @RequestBody final EpisodeReviewRequest request,
										  @LoginUser final Long userId) {
		EpisodeReviewCommand episodeReviewCommand = EpisodeReviewCommand.of(userId, request);
		episodesService.addReview(episodeId, episodeReviewCommand);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}

	@Operation(
            summary = "에피소드에 대한 리뷰 조회",
            description = "에피소드별 리뷰를 조회할 수 있습니다."
    )
	@GetMapping("/episodes/{episodeId}/reviews")
	public ResponseEntity<PageResponse<EpisodeReviewResponse>> getEpisodeReviews(@PathVariable(name = "episodeId") final Long episodeId,
																				 PageRequest request) {

		PageResponse<EpisodeReviewResponse> episodeReviews = episodeReviewRepository.getEpisodeReviews(episodeId, request);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(episodeReviews);
	}

	@Operation(
			summary = "에피소드 리뷰 수정",
			description = "작성했던 에피소드별 리뷰를 수정할 수 있습니다."
	)
	@PutMapping("/episodes/{episodeId}/reviews")
	public ResponseEntity<Void> updateReview(@PathVariable(name = "episodeId") Long episodeId,
                                            @Valid @RequestBody final EpisodeReviewRequest request,
                                            @LoginUser final Long userId) {
		EpisodeReviewCommand command = EpisodeReviewCommand.of(userId, request);
		episodesService.updateReview(episodeId, command);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}

	@Operation(
			summary = "에피소드 리뷰 삭제",
			description = "작성했던 에피소드별 리뷰를 삭제할 수 있습니다."
	)
	@DeleteMapping("/episodes/{episodeId}/reviews")
	public ResponseEntity<Void> deleteReview(@PathVariable(name = "episodeId") final Long episodeId,
											 @LoginUser final Long userId) {
		episodesService.deleteReview(episodeId, userId);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}
}
