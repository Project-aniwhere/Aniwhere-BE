package com.example.aniwhere.repository.anime.repository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.QAnime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnimeCustomRepositoryImpl implements AnimeCustomRepository{
    private final JPAQueryFactory queryFactory;

    public AnimeCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public static final QAnime anime = new QAnime("anime");

    @Override
    public List<AnimeDTO.AnimeGroupedByWeekdayDTO> findAllGroupedByWeekday(Integer year, Integer quarter) {
        List<Anime> allAnimes = queryFactory
                .selectFrom(anime)
                .where(
                        anime.releaseDate.year().loe(year), // 출시 연도 <= 요청 연도
                        anime.endDate.isNull().or(anime.endDate.year().goe(year)) // 종료 연도 >= 요청 연도 또는 null
                )
                .fetch();

        // 요청 year와 quarter에 해당하는 애니메이션 필터링
        List<Anime> filteredAnimes = allAnimes.stream()
                .filter(anime -> isQuarterInRange(anime.getReleaseDate(), anime.getEndDate(), year, quarter))
                .collect(Collectors.toList());

        // 요일-숫자 매핑
        Map<String, Integer> weekdayToCode = Map.of(
                "월요일", 1,
                "화요일", 2,
                "수요일", 3,
                "목요일", 4,
                "금요일", 5,
                "토요일", 6,
                "일요일", 7
        );

        // 요일순서
        List<String> weekdayOrder = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일");

        Map<String, List<Anime>> groupedByWeekday = filteredAnimes.stream()
                .collect(Collectors.groupingBy(
                        Anime::getWeekday,
                        LinkedHashMap::new, // LinkedHashMap: 순서 유지
                        Collectors.toList()
                ));

        return weekdayOrder.stream()
                .filter(groupedByWeekday::containsKey) // 없는 요일 제외
                .map(weekday -> AnimeDTO.AnimeGroupedByWeekdayDTO.builder()
                        .weekdayCode(weekdayToCode.get(weekday))
                        .animes(groupedByWeekday.get(weekday).stream()
                                .map(anime -> AnimeDTO.WeekdayAnimeDTO.builder()
                                        .animeId(anime.getAnimeId())
                                        .title(anime.getTitle())
                                        .poster(anime.getPoster())
                                        .weekday(anime.getWeekday())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 방영 기간 내에 특정 연도 및 분기가 포함되는지 확인
     */
    private boolean isQuarterInRange(LocalDate releaseDate, LocalDate endDate, Integer year, Integer quarter) {
        if (endDate == null) {
            endDate = LocalDate.now(); // 종료일이 없으면 현재 날짜로 간주
        }

        // 분기별 시작 월과 종료 월 계산
        int quarterStartMonth = (quarter - 1) * 3 + 1;
        int quarterEndMonth = quarterStartMonth + 2;

        LocalDate quarterStart = LocalDate.of(year, quarterStartMonth, 1);
        LocalDate quarterEnd = LocalDate.of(year, quarterEndMonth, quarterEndMonth == 2 ? 28 : 30); // 월별 마지막 날짜 계산

        // 요청된 분기와 방영 기간이 겹치는지 확인
        return !releaseDate.isAfter(quarterEnd) && !endDate.isBefore(quarterStart);
    }
}
