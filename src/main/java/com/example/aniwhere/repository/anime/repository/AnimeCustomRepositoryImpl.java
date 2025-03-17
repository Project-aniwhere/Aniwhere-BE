package com.example.aniwhere.repository.anime.repository;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.QAnime;
import com.example.aniwhere.domain.anime.QAnimeKeyword;
import com.example.aniwhere.domain.anime.dto.AnimeDTO;
import com.example.aniwhere.domain.anime.dto.AnimeSearchResponseDto;
import com.example.aniwhere.domain.anime.dto.QAnimeSearchResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

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

    @Override
    public PageResponse<AnimeSearchResponseDto> findByFilters(
            List<String> categories, List<Integer> quarters, String title, List<String> statuses, Integer year, PageRequest pageRequest) {

        // ✅ Spring Data의 PageRequest 직접 생성 (Cannot resolve method 'of()' 문제 해결)
        org.springframework.data.domain.PageRequest request = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());


        List<AnimeSearchResponseDto> results = queryFactory
                .select(new QAnimeSearchResponseDto(
                        anime.animeId,
                        anime.title,
                        convertStatus(anime.status),
                        anime.poster
                ))
                .from(anime)
                .leftJoin(QAnime.anime.keywords, QAnimeKeyword.animeKeyword) // ✅ 키워드 테이블 조인
                .where(
                        categoryIn(categories),
                        yearFilter(year),
                        quarterIn(quarters, year), // ✅ 방영중인 경우 해당 분기 포함 여부 검사
                        titleLike(title),
                        statusIn(statuses)
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        JPAQuery<Anime> countQuery = queryFactory
                .selectFrom(anime)
                .leftJoin(QAnime.anime.keywords, QAnimeKeyword.animeKeyword)
                .where(
                        categoryIn(categories),
                        yearFilter(year),
                        quarterIn(quarters, year),
                        titleLike(title),
                        statusIn(statuses)
                );

        Page<AnimeSearchResponseDto> page = PageableExecutionUtils.getPage(results, request, countQuery::fetchCount);

        return new PageResponse<>(page);
    }

    // ✅ LIKE 검색 적용 (대소문자 구분 없이 부분 검색)
    private BooleanExpression titleLike(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return QAnime.anime.title.likeIgnoreCase("%" + keyword.trim() + "%")
                    .or(QAnime.anime.keywords.any().keyword.likeIgnoreCase("%" + keyword.trim() + "%")); //키워드 추가
        }
        return null;
    }


    // ✅ 카테고리 필터 적용
    private BooleanExpression categoryIn(List<String> categories) {
        return (categories != null && !categories.isEmpty()) ? anime.animeCategories.any().category.categoryName.in(categories) : null;
    }

    // ✅ 방영 분기 필터 적용 (특정 연도 및 분기 포함 여부 검사)
    private BooleanExpression quarterIn(List<Integer> quarters, Integer year) {
        if (quarters == null || quarters.isEmpty() || year == null) return null;

        BooleanExpression condition = null;
        for (Integer quarter : quarters) {
            BooleanExpression quarterCondition = anime.releaseDate.loe(getQuarterEnd(year, quarter))
                    .and(anime.endDate.coalesce(LocalDate.now()).goe(getQuarterStart(year, quarter)));

            condition = (condition == null) ? quarterCondition : condition.or(quarterCondition);
        }

        return condition;
    }

    // ✅ 상태값 필터 적용 (변환 후 비교)
    private BooleanExpression statusIn(List<String> statuses) {
        return (statuses != null && !statuses.isEmpty()) ? convertStatus(anime.status).in(statuses) : null;
    }

    // ✅ 변경된 정책 반영 (status 변환)
    private StringExpression convertStatus(StringExpression status) {
        return new CaseBuilder()
                .when(status.eq("Returning Series")).then("방영중")
                .when(status.eq("Canceled")).then("제작 취소")
                .when(status.eq("In Production")).then("제작중")
                .when(status.isNull()).then("방영종료")
                .when(status.eq("Ended")).then("방영종료")
                .otherwise(status); // 혹시 모를 예외 처리
    }

    // ✅ 해당 연도 및 분기의 시작 날짜 반환
    private LocalDate getQuarterStart(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        return LocalDate.of(year, startMonth, 1);
    }

    // ✅ 해당 연도 및 분기의 종료 날짜 반환
    private LocalDate getQuarterEnd(int year, int quarter) {
        int endMonth = quarter * 3;
        return LocalDate.of(year, endMonth, endMonth == 2 ? 28 : 30);
    }

    private BooleanExpression yearFilter(Integer year) {
        if (year == null) return null; // 연도 필터가 없을 경우 무시

        return anime.releaseDate.year().loe(year) // ✅ 출시 연도가 year 이하
                .and(anime.endDate.isNull().or(anime.endDate.year().goe(year))); // ✅ 종료 연도가 year 이상이거나 NULL
    }

}
