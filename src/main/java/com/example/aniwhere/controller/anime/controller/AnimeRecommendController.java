package com.example.aniwhere.controller.anime.controller;

import com.example.aniwhere.service.anime.service.RecommendService;
import com.example.aniwhere.domain.recommendList.RecommendList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnimeRecommendController {

    private final RecommendService recommendService;

    @GetMapping("/recommend")
    public ResponseEntity<List<RecommendList>> getAnimeRecommendList() {
        List<RecommendList> lists = recommendService.getRecommendLists();
        return ResponseEntity.ok(lists);
    }


//    @GetMapping
//    public List<Anime> getRecommendations(@RequestParam String gender, @RequestParam int age) {
//        return recommendService.recommendAnimes(gender, age);
//    }

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
