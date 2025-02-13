package com.example.aniwhere.repository.animeReview;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.animeReview.dto.AnimeReviewResponse;
import com.example.aniwhere.domain.animeReview.dto.QAnimeReviewResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.domain.animeReview.QAnimeReview.animeReview;


@Repository
@RequiredArgsConstructor
public class AnimeReviewRepositoryImpl implements AnimeReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<AnimeReviewResponse> getAnimeReviews(Long animeId, PageRequest pageRequest) {

        org.springframework.data.domain.PageRequest request = pageRequest.of();

        List<AnimeReviewResponse> reviews = queryFactory
                .select(new QAnimeReviewResponse(
                        animeReview.id,
                        animeReview.rating,
                        animeReview.content,
                        animeReview.user.nickname
                ))
                .from(animeReview)
                .where(animeReview.anime.animeId.eq(animeId))
                .offset(request.getOffset())
                .limit(request.getPageSize())
                .fetch();

        JPAQuery<AnimeReview> countQuery = queryFactory
                .select(animeReview)
                .from(animeReview)
                .where(animeReview.anime.animeId.eq(animeId));

        Page<AnimeReviewResponse> page = PageableExecutionUtils.getPage(reviews, request, countQuery::fetchCount);

        return new PageResponse<>(page);
    }
}
