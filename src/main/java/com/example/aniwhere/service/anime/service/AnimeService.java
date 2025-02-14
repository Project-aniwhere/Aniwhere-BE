package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewRequest;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewResponse;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.BusinessException;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.animeReview.AnimeReviewRepository;
import com.example.aniwhere.repository.casting.repository.CastingRepository;
import com.example.aniwhere.repository.rating.repository.RatingRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO.*;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;

import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;
    private final CastingRepository castingRepository;
    private final RatingRepository ratingRepository;
    private final AnimeReviewRepository animeReviewRepository;
    private final UserRepository userRepository;


    public Double calculateAverageRating(List<AnimeResponseDTO.RatingDTO> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToDouble(r -> r.getRating().doubleValue()) // BigDecimal → double 변환
                .sum();

        return totalRating / reviews.size();
    }

    private QuarterAnimeResponseDTO convertToDTO(Anime anime) {
        return QuarterAnimeResponseDTO.builder()
                .animeId(anime.getAnimeId())
                .title(anime.getTitle())
                .poster(anime.getPoster())
                .weekday(anime.getWeekday())
                .build();
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

    public PageResponse<AnimeReviewResponse> getAnimeReviews(Long animeId, PageRequest request) {
        return animeReviewRepository.getAnimeReviews(animeId, request);
    }
    @Transactional
    public void addAnimeReview(Long animeId, Long userId, AnimeReviewRequest request) {
        Anime anime = animeRepository.findById(animeId).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_ANIME));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

        chkAlreadyExistReview(anime, user);

        anime.addReview(request.rating());

        AnimeReview animeReview = AnimeReview.builder()
                .anime(anime)
                .user(user)
                .rating(request.rating())
                .content(request.content())
                .build();

        animeReviewRepository.save(animeReview);
    }

    @Transactional
    public void updateAnimeReview(Long animeId,Long animeReviewId, AnimeReviewRequest request, Long userId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_ANIME));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

        AnimeReview animeReview = animeReviewRepository.findById(animeReviewId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANIME_REVIEW));

        chkSameUser(user, animeReview);

        anime.updateReview(animeReview.getRating(), request.rating());
        animeReview.updateRatingAndContent(request.rating(), request.content());
    }

    public void deleteAnimeReview(Long animeId, Long animeReviewId, Long userId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_ANIME));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

        AnimeReview animeReview = animeReviewRepository.findById(animeReviewId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANIME_REVIEW));

        chkSameUser(user, animeReview);
        chkSameAnime(anime, animeReview);

        anime.deleteAnimeReview(animeReview.getRating());
        animeReviewRepository.delete(animeReview);
    }

    private void chkAlreadyExistReview(Anime anime, User user) {
        if (animeReviewRepository.existsByAnimeAndUser(anime, user)){
            throw new UserException(ALREADY_EXIST_EPISODE_REVIEW);
        }
    }

    private void chkSameUser(User user, AnimeReview animeReview) {
        if (!user.getId().equals(animeReview.getUser().getId())) {
            throw new ResourceNotFoundException(UNAUTHORIZED);
        }
    }

    private void chkSameAnime(Anime anime, AnimeReview animeReview) {
        if (!anime.getAnimeId().equals(animeReview.getAnime().getAnimeId())) {
            throw new ResourceNotFoundException(ANIME_REVIEW_NOT_BELONGS_TO_ANIME);
        }
    }
}

