package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.EpisodesInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodesRepositoryCustom {
	PageResponse<EpisodesDto> getEpisodes(Long animeId, PageRequest request);
	List<EpisodesInfoDto> getEpisodeById(Long episodeId);
}
