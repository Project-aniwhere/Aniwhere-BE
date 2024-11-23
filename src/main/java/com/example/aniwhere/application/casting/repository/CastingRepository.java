package com.example.aniwhere.application.casting.repository;

import com.example.aniwhere.domain.casting.Casting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CastingRepository extends JpaRepository<Casting, Long> {
    List<Casting> findCastingByAnime_animeId(Long animeId);
}
