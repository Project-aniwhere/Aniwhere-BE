package com.example.aniwhere.controller.anime.controller;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import com.example.aniwhere.domain.recommendList.RecommendListDTO;
import com.example.aniwhere.service.anime.service.RecommendService;
import com.example.aniwhere.domain.recommendList.RecommendList;
import com.example.aniwhere.service.division.DivisionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnimeRecommendController {

    private final RecommendService recommendService;
    private final DivisionService divisionService;

    @Operation(
            summary = "애니메이션 추천 리스트",
            description = "로그인 여부에 따라 개인화된 추천 리스트 또는 일반 추천 리스트를 반환합니다."
    )
    @GetMapping(value = {"/recommend", "/recommend/{nickname}"})
    public ResponseEntity<?> getAnimeRecommendations(
            @PathVariable(required = false) String nickname,
            @RequestParam(defaultValue = "false") boolean refresh) {

        if (nickname != null && !nickname.isEmpty()) {
            if (refresh) {
                recommendService.evictUserRecommendationCache(nickname);
            }

            // 개인화 추천 리스트
            List<Anime> personalizedRecommendations = recommendService.recommendAnimesForUser(nickname);

            // 연령대 및 성별 기반 추천 리스트
            List<AnimeSummaryDTO> groupRecommendations = divisionService.recommendAnimes(nickname);

            // JSON 응답을 위한 DTO 생성
            Map<String, Object> response = new HashMap<>();
            response.put("nickname", nickname);
            response.put("personalizedRecommendations", personalizedRecommendations);
            response.put("groupRecommendations", groupRecommendations);

            return ResponseEntity.ok(response);
        }

        // 로그인하지 않은 경우 (nickname이 없는 경우)
        List<RecommendListDTO> generalRecommendations = recommendService.getRecommendLists();
        return ResponseEntity.ok(Map.of("recommendations", generalRecommendations));
    }

    @PostMapping("/recommend")
    public ResponseEntity<RecommendList> addAnimeRecommendList(@RequestBody RecommendList recommendList) {
        RecommendList savedList = recommendService.insertRecommendList(recommendList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedList);
    }

    @DeleteMapping("/recommend/{id}")
    public ResponseEntity<String> deleteAnimeRecommendList(@PathVariable Long id) {
        recommendService.deleteRecommendList(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/recommend/{id}")
    public ResponseEntity<RecommendList> updateAnimeRecommendList(
            @RequestBody RecommendList recommendList,
            @PathVariable Long id
    ) {
        RecommendList updatedList = recommendService.updateRecommendList(id, recommendList);
        if (updatedList != null) {
            return ResponseEntity.ok(updatedList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(
            summary = "인기 애니메이션 추천",
            description = "이번 분기 인기 애니메이션 리스트를 반환합니다"
    )
    @GetMapping("/trend")
    public ResponseEntity<List<AnimeSummaryDTO>> getPopularAnime() {
        List<AnimeSummaryDTO> popularAnime = recommendService.getPopularAnime(20);
        return ResponseEntity.ok(popularAnime);
    }
}
