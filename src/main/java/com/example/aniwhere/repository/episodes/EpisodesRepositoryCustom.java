package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodesRepositoryCustom {
	PageResponse<EpisodesDto> getEpisodes(Long animeId, PageRequest request);
}
