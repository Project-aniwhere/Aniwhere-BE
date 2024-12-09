package com.example.aniwhere.controller.episodes;

import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.service.episodes.EpisodesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Episode", description = "에피소드 관련 API")
public class EpisodesApiController {

	private final EpisodesService episodesService;
	@Operation(
			summary = "애니메이션 ID값에 대한 전체 에피소드 조회",
			description = "특정 애니메이션의 모든 에피소드를 페이지 단위로 조회합니다."
	)
	@GetMapping("/episodes/{animeId}")
	public Page<EpisodesDto> getEpisodes(@PathVariable(name = "animeId") Long animeId, Pageable pageable) {
		return episodesService.getEpisodes(animeId, pageable);
	}
}
