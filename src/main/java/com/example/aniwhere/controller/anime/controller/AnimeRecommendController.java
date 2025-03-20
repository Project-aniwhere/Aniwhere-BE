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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class AnimeRecommendController {

    private final RecommendService recommendService;
    private final DivisionService divisionService;

    @Operation(
            summary = "애니메이션 추천 리스트",
            description = "개인화없는 추천 리스트를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<List<RecommendListDTO>> getAnimeRecommendList() {
        List<RecommendListDTO> lists = recommendService.getRecommendLists();
        return ResponseEntity.ok(lists);
    }

    @Operation(
            summary = "그룹별 애니메이션 추천",
            description = "사용자의 연령대와 성별에 맞춰 애니메이션 리스트를 추천합니다."
    )
    @GetMapping("/division/{nickname}")
    public ResponseEntity<List<AnimeSummaryDTO>> getAnimeRecommendByDivision(@PathVariable String nickname) {
        List<AnimeSummaryDTO> list = divisionService.recommendAnimes(nickname);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<List<Anime>> getAnimeRecommendByNickname(
            @PathVariable String nickname,
            @RequestParam(defaultValue = "false") boolean refresh) {
        if (refresh) {
            recommendService.evictUserRecommendationCache(nickname);
        }
        List<Anime> recommendations = recommendService.recommendAnimesForUser(nickname);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * 관리자용
     */
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
}
