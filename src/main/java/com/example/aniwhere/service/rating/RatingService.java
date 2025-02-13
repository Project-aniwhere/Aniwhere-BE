package com.example.aniwhere.service.rating;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.rating.Rating;
import com.example.aniwhere.domain.rating.RatingDTO;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.rating.repository.RatingRepository;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final AnimeRepository animeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void submitRating(RatingDTO ratingDTO) {
        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ratingDTO.getUserId()));
        Anime anime = animeRepository.findById(ratingDTO.getAnimeId())
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with ID: " + ratingDTO.getAnimeId()));

        Rating existingRating = ratingRepository.findByUserAndAnime(user, anime).orElse(null);

        if (existingRating != null) {
            ratingRepository.delete(existingRating);
        }
            Rating newRating = Rating.builder()
                    .user(user)
                    .anime(anime)
                    .rating(ratingDTO.getRating())
                    .build();

            ratingRepository.save(newRating);
    }

    public Double getUserRating(Long userId, Long animeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with ID: " + animeId));

        return ratingRepository.findByUserAndAnime(user, anime)
                .map(Rating::getRating) // Rating 객체에서 점수를 가져옴
                .orElse((double) 0);
    }
}
