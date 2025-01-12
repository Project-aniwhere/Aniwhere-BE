package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import com.example.aniwhere.domain.like.Like;
import com.example.aniwhere.domain.recommendList.RecommendListDTO;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.anime.repository.RecommendListRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.division.Division;
import com.example.aniwhere.repository.division.DivisionRepository;
import com.example.aniwhere.domain.recommendList.RecommendList;
import com.example.aniwhere.repository.like.LikeRepository;
import com.example.aniwhere.repository.user.UserRepository;
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
    private final LikeRepository likeRepository;


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

    public RecommendList insertRecommendList(RecommendList recommendList) {
        return recommendListRepository.save(recommendList);
    }

    public void deleteRecommendList(long id) {
        recommendListRepository.deleteById(id);
    }

    public RecommendList updateRecommendList(Long id, RecommendList recommendList) {
        recommendList.setId(id);
        return recommendListRepository.save(recommendList);
    }

    @Cacheable(value = "recommendations", key = "#nickname")
    public List<Anime> recommendAnimesForUser(String nickname) {
        List<Like> likes = likeRepository.findByUser_Nickname(nickname);

        // 좋아요한 애니메이션 리스트 추출
        List<Anime> userLikedAnimes = likes.stream()
                .map(Like::getAnime)
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        if (userLikedAnimes.isEmpty()) {
            throw new IllegalArgumentException("No liked animes found for user with nickname: " + nickname);
        }

        return animeRecommender.recommend(userLikedAnimes, 10);
    }

    @CacheEvict(value = "recommendations", key = "#nickname")
    public void evictUserRecommendationCache(String nickname) {
    }

    private String getDivisionName(String gender, int age) {
        if (age >= 10 && age < 20) {
            return gender.equalsIgnoreCase("male") ? "Male10" : "Female10";
        } else if (age >= 20 && age < 30) {
            return gender.equalsIgnoreCase("male") ? "Male20" : "Female20";
        } else if (age >= 30 && age < 40) {
            return gender.equalsIgnoreCase("male") ? "Male30" : "Female30";
        } else if (age >= 40) {
            return gender.equalsIgnoreCase("male") ? "Male40" : "Female40";
        } else {
            throw new IllegalArgumentException("Invalid age: " + age);
        }
    }
}
