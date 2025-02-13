package com.example.aniwhere.repository.rating.repository;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.rating.Rating;
import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByAnime_AnimeId(Long animeId);

    Optional<Rating> findByUserAndAnime(User user, Anime anime);
}
