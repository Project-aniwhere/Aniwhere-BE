package com.example.aniwhere.repository.anime.repository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.QAnime;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
    public Map<Integer, List<Anime>> findAllGroupedByWeekday(Integer year, Integer quarter) {
        List<Anime> allAnimes = queryFactory
                .selectFrom(anime)
                .where(
                        anime.releaseDate.year().loe(year), // 출시 연도 <= 요청 연도
                        anime.endDate.isNull().or(anime.endDate.year().goe(year)), // 종료 연도 >= 요청 연도 또는 null
                        anime.airingQuarter.eq(quarter) // 분기 조건 일치
                )
                .fetch();

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

        Map<String, List<Anime>> groupedByWeekday = allAnimes.stream()
                .collect(Collectors.groupingBy(
                        Anime::getWeekday,
                        LinkedHashMap::new, // LinkedHashMap: 순서 유지
                        Collectors.toList()
                ));

        return weekdayOrder.stream()
                .filter(groupedByWeekday::containsKey) // 없는 요일 제외
                .collect(Collectors.toMap(
                        weekdayToCode::get,
                        groupedByWeekday::get,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}
