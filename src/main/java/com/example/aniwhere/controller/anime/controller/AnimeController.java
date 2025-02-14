package com.example.aniwhere.controller.anime.controller;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.anime.dto.AnimeDTO.*;
import com.example.aniwhere.domain.history.dto.HistoryUserDto;
import com.example.aniwhere.domain.anime.dto.AnimeQuarterDTO;
import com.example.aniwhere.service.anime.service.AnimeService;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.InvalidInputException;
import com.example.aniwhere.service.history.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnimeController {

    private final AnimeService animeService;
    private final HistoryService historyService;

    /**
     *
     * @param year 방영년도
     * @param quarter 방영분기
     * @return
     */
    @GetMapping("/anime/quarter")
    public ResponseEntity<Map<String, List<QuarterAnimeResponseDTO>>> getAnimeByQuarter(@RequestParam int year, @RequestParam int quarter) {
        if (year < 1990 ||year > 2030) {
            throw new InvalidInputException("연도는 1990년 이상, 2030년 이하여야 합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (quarter < 1 || quarter > 4) {
            throw new InvalidInputException("분기는 1 이상, 4 이하여야 합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        Map<String, List<QuarterAnimeResponseDTO>> animeGroupedByWeekday = animeService.getAnimeByYearAndQuarter(year, quarter);

        return ResponseEntity.ok(animeGroupedByWeekday);
    }

    /**
     *
     * @return WeekdayAnimeDTO
     */
    @Operation(
            summary = "요일별 애니메이션 조회",
            description = "요일별 애니메이션을 월요일(1) ~ 일요일(7) 순으로 조회합니다."
    )
    @GetMapping("/anime/weekday")
    public ResponseEntity<List<AnimeGroupedByWeekdayDTO>> getWeekdayAnimeList(@RequestParam(required = false) Integer year,
                                                                                   @RequestParam(required = false) Integer quarter){
        List<AnimeGroupedByWeekdayDTO> animeResponse = animeService.getAnimeWeekdayList(year, quarter);
        return ResponseEntity.ok(animeResponse);
    }


    @Operation(
            summary = "[사용자용] 작품 요청",
            description = "관리자에게 작품 요청을 보낸다."
    )
    @PostMapping("/anime/requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> requestAnime(@LoginUser final Long userId,
                                             @RequestBody final HistoryUserDto dto) {
        historyService.requestAnime(userId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

}
