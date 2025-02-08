package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendListRepository recommendListRepository;
    private final AnimeRecommender animeRecommender;
    private final PickedAnimeRepository pickedAnimeRepository; // 기존 LikeRepository 대신 PickedAnimeRepository 사용

    /**
     * 모든 추천 리스트를 가져옴
     */
    public List<RecommendListDTO> getRecommendLists() {
        List<RecommendList> recommendLists = recommendListRepository.findAll();

        return recommendLists.stream()
                .map(recommendList -> RecommendListDTO.builder()
                        .id(recommendList.getId())
                        .title(recommendList.getTitle())
                        .description(recommendList.getDescription())
                        .animes(recommendList.getAnimes().stream()
                                .map(anime -> AnimeSummaryDTO.builder()
                                        .animeId(anime.getAnimeId())
                                        .title(anime.getTitle())
                                        .poster(anime.getPoster())
                                        .build()
                                )
                                .collect(Collectors.toList())
                        )
                        .build()
                )
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
    @Cacheable(value = "recommendations", key = "#nickname")
    public List<Anime> recommendAnimesForUser(String nickname) {
        List<PickedAnime> pickedAnimes = pickedAnimeRepository.findByUserNickname(nickname); // PickedAnime 사용

        // PickedAnime 리스트에서 Anime 리스트로 변환
        List<Anime> pickedAnimeList = pickedAnimes.stream()
                .map(PickedAnime::getAnime) // PickedAnime에서 Anime 추출
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        if (pickedAnimeList.isEmpty()) {
            throw new IllegalArgumentException("No picked animes found for user with nickname: " + nickname);
        }

        return animeRecommender.recommend(pickedAnimeList, 10); // ✅ 변환 후 전달
    }

    /**
     * 유저 추천 캐시 제거
     */
    @CacheEvict(value = "recommendations", key = "#nickname")
    public void evictUserRecommendationCache(String nickname) {
        log.info("Cleared recommendation cache for user: {}", nickname);
    }
}
