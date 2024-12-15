package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodesRepositoryCustom {
	Page<EpisodesDto> getEpisodes(Long animeId, Pageable pageable);
}
