package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.domain.recommendList.RecommendListDTO;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.anime.repository.RecommendListRepository;
import com.example.aniwhere.repository.pickedAnime.PickedAnimeRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.recommendList.RecommendList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.aniwhere.domain.category.dto.CategoryDTO.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendListRepository recommendListRepository;
    private final AnimeRecommender animeRecommender;
    private final PickedAnimeRepository pickedAnimeRepository;
    private final AnimeRepository animeRepository;

    /**
     * 모든 추천 리스트를 가져옴
     */
    @Transactional
    public List<RecommendListDTO> getRecommendLists() {
        List<RecommendList> recommendLists = recommendListRepository.findAll();

        Collections.shuffle(recommendLists);

        return recommendLists.stream()
                .limit(5)
                .map(recommendList -> {
                    List<AnimeSummaryDTO> allAnimes = recommendList.getAnimes().stream()
                            .map(recommendListAnime -> {
                                Anime anime = recommendListAnime.getAnime();
                                return AnimeSummaryDTO.builder()
                                        .animeId(anime.getAnimeId())
                                        .title(anime.getTitle())
                                        .poster(anime.getPoster())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return RecommendListDTO.builder()
                            .id(recommendList.getId())
                            .title(recommendList.getTitle())
                            .description(recommendList.getDescription())
                            .animes(allAnimes)
                            .build();
                })
                .collect(Collectors.toList());
    }


    /**
     * 추천 리스트 삽입
     */
    public RecommendList insertRecommendList(RecommendList recommendList) {
        return recommendListRepository.save(recommendList);
    }

    /**
     * 추천 리스트 삭제
     */
    public void deleteRecommendList(long id) {
        recommendListRepository.deleteById(id);
    }

    /**
     * 추천 리스트 업데이트
     */
    public RecommendList updateRecommendList(Long id, RecommendList recommendList) {
        recommendList.setId(id);
        return recommendListRepository.save(recommendList);
    }

    /**
     * 유저의 `PickedAnime` 목록을 기반으로 애니메이션 추천
     */
//    @Cacheable(value = "recommendations", key = "#nickname")
    public List<AnimeSummaryDTO> recommendAnimesForUser(String nickname) {
        List<PickedAnime> pickedAnimes = pickedAnimeRepository.findByUserNickname(nickname);
        List<Anime> pickedAnimeList = pickedAnimes.stream()
                .map(PickedAnime::getAnime)
                .distinct()
                .collect(Collectors.toList());

        if (pickedAnimeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Anime> recommendedAnimes = animeRecommender.recommend(pickedAnimeList, 10);

        return recommendedAnimes.stream().map(anime -> {
            double averageRating = 0.0;
            if (anime.getScoreCnt() != null && anime.getScoreCnt() > 0) {
                averageRating = (double) anime.getTotalScore() / anime.getScoreCnt();
            }

            String latestReview = anime.getReviews().stream()
                    .max(Comparator.comparing(AnimeReview::getCreatedAt))
                    .map(AnimeReview::getContent)
                    .orElse("No reviews yet");

            List<CategoryResponseDTO> categories = anime.getAnimeCategories() == null
                    ? new ArrayList<>()
                    : anime.getAnimeCategories().stream()
                    .map(animeCategory -> CategoryResponseDTO.of(animeCategory.getCategory()))
                    .collect(Collectors.toList());

            return AnimeSummaryDTO.builder()
                    .animeId(anime.getAnimeId())
                    .title(anime.getTitle())
                    .description(anime.getDescription())
                    .poster(anime.getPoster())
                    .studio(anime.getStudio())
                    .episodes(anime.getEpisodeCount())
                    .releaseDate(anime.getEndDate())
                    .averageRating(averageRating)
                    .latestReview(latestReview)
                    .categories(categories)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 유저 추천 캐시 제거
     */
    @CacheEvict(value = "recommendations", key = "#nickname")
    public void evictUserRecommendationCache(String nickname) {
        log.info("Cleared recommendation cache for user: {}", nickname);
    }

    public List<AnimeSummaryDTO> getPopularAnime(int limit) {
        List<Object[]> results = animeRepository.findPopularAnime(limit);

        return results.stream().map(this::convertToDTO).toList();
    }

    private AnimeSummaryDTO convertToDTO(Object[] obj) {
        return new AnimeSummaryDTO(
                ((Number) obj[0]).longValue(),
                (String) obj[1],
                (String) obj[2],
                (String) obj[3],
                (String) obj[4],
                obj[5] != null ? ((Number) obj[5]).intValue() : 0,
                obj[6] != null ? ((java.sql.Date) obj[6]).toLocalDate() : null,
                obj[7] != null ? ((Number) obj[7]).doubleValue() : 0.0,
                obj[8] != null ? (String) obj[8] : "No reviews yet",
                obj.length > 9 && obj[9] != null ? (List<CategoryResponseDTO>) obj[9] : new ArrayList<>()
        );
    }
}
