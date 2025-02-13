package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.repository.casting.repository.CastingRepository;
import com.example.aniwhere.repository.rating.repository.RatingRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO.*;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;

import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;
    private final CastingRepository castingRepository;
    private final RatingRepository ratingRepository;


    public Double calculateAverageRating(List<AnimeResponseDTO.RatingDTO> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToDouble(r -> r.getRating().doubleValue()) // BigDecimal → double 변환
                .sum();

        return totalRating / reviews.size();
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

        List<AnimeResponseDTO.RatingDTO> ratings = ratingRepository.findByAnime_AnimeId(animeId).stream()
                .map(review -> AnimeResponseDTO.RatingDTO.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getUser().getProviderId().toString())
                        .rating(review.getRating())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        Double averageRating = calculateAverageRating(ratings);

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
                .runningTime(anime.getRunningTime())
                .status(anime.getStatus())
                .trailer(anime.getTrailer())
                .description(anime.getDescription())
                .poster(anime.getPoster())
                .airingQuarter(anime.getAiringQuarter())
                .isAdult(anime.getIsAdult())
                .duration(anime.getDuration())
                .weekday(anime.getWeekday())
                .categories(anime.getAnimeCategories().stream()
                        .map(animeCategory -> animeCategory.getCategory().getCategoryName())
                        .collect(Collectors.toSet()))
                .castings(castings)
                .ratings(ratings)
                .episodes(null)
                .averageRating(averageRating)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AnimeGroupedByWeekdayDTO> getAnimeWeekdayList(Integer year, Integer quarter) {
        // 기본값 설정: 현재 연도와 분기
        if (year == null) {
            year = Year.now().getValue();
        }
        if (quarter == null) {
            quarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1; // 현재 월 기준 분기 계산
        }

        return animeRepository.findAllGroupedByWeekday(year, quarter);
    }
}

