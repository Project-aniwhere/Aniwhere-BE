package com.example.aniwhere.domain.anime.controller;

import com.example.aniwhere.domain.anime.dto.AnimeQuarterDTO.*;
import com.example.aniwhere.domain.anime.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AnimeQuarterController {

    private final AnimeService animeService;

    /**
     *
     * @param year 방영년도
     * @param quarter 방영분기
     * @return
     */
    @GetMapping("/anime/quarter")
    public ResponseEntity<Map<String, List<AnimeResponseDTO>>> getAnimeByQuarter(@RequestParam int year, @RequestParam int quarter) {
        Map<String, List<AnimeResponseDTO>> animeGroupedByWeekday = animeService.getAnimeByYearAndQuarter(year, quarter);

        return ResponseEntity.ok(animeGroupedByWeekday);
    }

}
