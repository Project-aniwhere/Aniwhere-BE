package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewRequest;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewResponse;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.animeReview.AnimeReviewRepository;
import com.example.aniwhere.repository.casting.repository.CastingRepository;
import com.example.aniwhere.repository.episodes.EpisodesRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO.*;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;

import com.example.aniwhere.repository.pickedAnime.PickedAnimeRepository;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
    private final AnimeReviewRepository animeReviewRepository;
    private final UserRepository userRepository;
    private final EpisodesRepository episodesRepository;
    private final PickedAnimeRepository pickedAnimeRepository;


    public Double calculateAverageRating(List<AnimeReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToDouble(AnimeReview::getRating) // BigDecimal → double 변환
                .sum();

        return totalRating / reviews.size();
    }

    @Transactional(readOnly = true)
    public AnimeResponseDTO getAnimeById(long animeId, Long userId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_ANIME));

        List<AnimeResponseDTO.CastingDTO> castings = castingRepository.findCastingByAnime_animeId(animeId).stream()
                .map(casting -> AnimeResponseDTO.CastingDTO.builder()
                        .castingId(casting.getCastingId())
                        .characterName(casting.getCharacterName())
                        .characterDescription(casting.getCharacterDescription())
                        .voiceActorName(casting.getVoiceActor().getName())
                        .build())
                .collect(Collectors.toList());

        Double averageRating = calculateAverageRating(anime.getReviews());
        boolean ispick = false;

        if (userId != null){
            Optional<PickedAnime> picked = pickedAnimeRepository.findByAnime_AnimeIdAndUserId(animeId, userId);
            if (picked.isPresent()) {
                ispick = true;
            }
        }

        // 페이지 요청 시 리뷰 3개만 조회하도록 설정
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);  // 1페이지 (내부적으로 0 기반 인덱스로 변환)
        pageRequest.setSize(3);  // 3개만 조회
        pageRequest.setDirection(Sort.Direction.DESC);  // 최신순 정렬

        PageResponse<AnimeReviewResponse> reviewPage = animeReviewRepository.getAnimeReviews(animeId, pageRequest);
        List<AnimeReviewResponse> reviews = reviewPage.getContent();

        PageRequest episodePage = new PageRequest();
        episodePage.setPage(1);
        episodePage.setSize(40);
        episodePage.setDirection(Sort.Direction.DESC);

        PageResponse<EpisodesDto> episodes = episodesRepository.getEpisodes(animeId, episodePage);

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
                .episodes(episodes.getContent())
                .runningTime(anime.getRunningTime())
                .status(anime.getStatus())
                .trailer(anime.getTrailer())
                .description(anime.getDescription())
                .poster(anime.getPoster())
                .ispicked(ispick)
                .airingQuarter(anime.getAiringQuarter())
                .isAdult(anime.getIsAdult())
                .duration(anime.getDuration())
                .weekday(anime.getWeekday())
                .categories(anime.getAnimeCategories().stream()
                        .map(animeCategory -> animeCategory.getCategory().getCategoryName())
                        .collect(Collectors.toSet()))
                .castings(castings)
                .averageRating(averageRating)
                .reviews(reviews)
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

