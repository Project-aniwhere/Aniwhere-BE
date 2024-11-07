package com.example.aniwhere.application.casting.repository;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.casting.Casting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CastingRepository extends JpaRepository<Anime, Long> {
    List<Casting> findCastingByAnimeId(Long animeId);
}
