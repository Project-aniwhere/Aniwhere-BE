package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.repository.casting.repository.CastingRepository;
import com.example.aniwhere.repository.review.repository.ReviewRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO.*;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;
    private final CastingRepository castingRepository;
    private final ReviewRepository reviewRepository;


    public BigDecimal calculateAverageRating(List<AnimeResponseDTO.ReviewDTO> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalRating = reviews.stream()
                .map(AnimeResponseDTO.ReviewDTO::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalRating.divide(new BigDecimal(reviews.size()), 2, RoundingMode.HALF_UP);
    }
    @Transactional(readOnly = true)
    public AnimeResponseDTO getAnimeById(long animeId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 애니메이션에 대한 정보를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_USER));

        List<AnimeResponseDTO.CastingDTO> castings = castingRepository.findCastingByAnime_animeId(animeId).stream()
                .map(casting -> AnimeResponseDTO.CastingDTO.builder()
                        .castingId(casting.getCastingId())
                        .characterName(casting.getCharacterName())
                        .characterDescription(casting.getCharacterDescription())
                        .voiceActorName(casting.getVoiceActor().getName()) // 성우 이름을 포함한 캐스팅 정보
                        .build())
                .collect(Collectors.toList());

        List<AnimeResponseDTO.ReviewDTO> reviews = reviewRepository.findByAnime_AnimeId(animeId).stream()
                .map(review -> AnimeResponseDTO.ReviewDTO.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getUser().getProviderId().toString())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        BigDecimal averageRating = calculateAverageRating(reviews);

        return AnimeResponseDTO.builder()
                .animeId(anime.getAnimeId())
                .title(anime.getTitle())
                .director(anime.getDirector())
                .characterDesign(anime.getCharacterDesign())
                .musicDirector(anime.getMusicDirector())
                .animationDirector(anime.getAnimationDirector())
                .script(anime.getScript())
                .producer(anime.getProducer())
                .studio(anime.getStudio())
                .releaseDate(anime.getReleaseDate())
                .endDate(anime.getEndDate())
                .episodeNum(anime.getEpisodesNum())
                .runningTime(anime.getRunningTime())
                .status(anime.getStatus())
                .trailer(anime.getTrailer())
                .description(anime.getDescription())
                .poster(anime.getPoster())
                .airingQuarter(anime.getAiringQuarter())
                .isAdult(anime.getIsAdult())
                .duration(anime.getDuration())
                .weekday(anime.getWeekday())
                .categories(anime.getCategories().stream()
                        .map(Category::getCategoryName)
                        .collect(Collectors.toSet()))
                .castings(castings)
                .reviews(reviews)
                .episodes(null)
                .averageRating(averageRating)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<WeekdayAnimeDTO>> getAnimeWeekdayList() {
        Map<Integer, List<Anime>> groupedByWeekday = animeRepository.findAllGroupedByWeekday();
        return groupedByWeekday.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(anime -> WeekdayAnimeDTO.builder()
                                        .animeId(anime.getAnimeId())
                                        .title(anime.getTitle())
                                        .poster(anime.getPoster())
                                        .weekday(anime.getWeekday())
                                        .build())
                                .collect(Collectors.toList()),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}

