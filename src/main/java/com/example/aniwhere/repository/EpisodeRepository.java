package com.example.aniwhere.repository;

import com.example.aniwhere.domain.episode.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findCastingByAnime_animeId(Long animeId);
}
