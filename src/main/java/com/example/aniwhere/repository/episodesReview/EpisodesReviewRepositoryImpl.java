package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import com.example.aniwhere.domain.episodeReviews.dto.QEpisodeReviewResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.domain.episodeReviews.QEpisodeReviews.episodeReviews;

@Repository
@RequiredArgsConstructor
public class EpisodesReviewRepositoryImpl implements EpisodesReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<EpisodeReviewResponse> getEpisodeReviews(Long episodeId, Pageable pageable) {
		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.episodes.id.eq(episodeId))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.episodes.id.eq(episodeId));

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchCount);
	}

	@Override
	public Page<EpisodeReviewResponse> getUserEpisodeReviews(String nickname, Pageable pageable) {
		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.user.nickname.eq(nickname))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.user.nickname.eq(nickname));

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchCount);
	}

	@Override
	public Page<EpisodeReviewResponse> getMyEpisodeReviews(Long userId, Pageable pageable) {
		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.user.id.eq(userId))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.user.id.eq(userId));

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchCount);
	}
}
