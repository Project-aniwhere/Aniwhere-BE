package com.example.aniwhere.controller.episodes;

import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.service.episodes.EpisodesService;
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
@Tag(name = "Mail", description = "에피소드 관련 API")
public class EpisodesApiController {

	private final EpisodesService episodesService;

	@GetMapping("/episodes/{animeId}")
	public Page<EpisodesDto> getEpisodes(@PathVariable(name = "animeId") Long animeId, Pageable pageable) {
		return episodesService.getEpisodes(animeId, pageable);
	}
}
