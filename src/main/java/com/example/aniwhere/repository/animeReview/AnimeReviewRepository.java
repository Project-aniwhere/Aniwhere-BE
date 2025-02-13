package com.example.aniwhere.repository.animeReview;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimeReviewRepository extends JpaRepository<AnimeReview, Long>, AnimeReviewRepositoryCustom {
    boolean existsByAnimeAndUser(Anime anime, User user);
}
