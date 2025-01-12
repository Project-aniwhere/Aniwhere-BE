package com.example.aniwhere.repository.pickedAnime;

import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickedAnimeRepository extends JpaRepository<PickedAnime, Long>, PickedAnimeRepositoryCustom {

	Optional<PickedAnime> findByAnime_AnimeIdAndUserId(Long animeId, Long userId);
}
