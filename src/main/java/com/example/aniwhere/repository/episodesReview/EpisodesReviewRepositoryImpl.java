package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
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
	public PageResponse<EpisodeReviewResponse> getEpisodeReviews(Long episodeId, PageRequest pageRequest) {

		org.springframework.data.domain.PageRequest request = pageRequest.of();

		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.episodes.id.eq(episodeId))
				.offset(request.getOffset())
				.limit(request.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.episodes.id.eq(episodeId));

		Page<EpisodeReviewResponse> page = PageableExecutionUtils.getPage(reviews, request, countQuery::fetchCount);

		return new PageResponse<>(page);
	}

	@Override
	public PageResponse<EpisodeReviewResponse> getUserEpisodeReviews(String nickname, PageRequest pageRequest) {

		org.springframework.data.domain.PageRequest request = pageRequest.of();

		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.user.nickname.eq(nickname))
				.offset(request.getOffset())
				.limit(request.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.user.nickname.eq(nickname));

		Page<EpisodeReviewResponse> page = PageableExecutionUtils.getPage(reviews, request, countQuery::fetchCount);

		return new PageResponse<>(page);
	}

	@Override
	public PageResponse<EpisodeReviewResponse> getMyEpisodeReviews(Long userId, PageRequest pageRequest) {

		org.springframework.data.domain.PageRequest request = pageRequest.of();

		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.nickname
				))
				.from(episodeReviews)
				.where(episodeReviews.user.id.eq(userId))
				.offset(request.getOffset())
				.limit(request.getPageSize())
				.fetch();

		JPAQuery<EpisodeReviews> countQuery = queryFactory
				.select(episodeReviews)
				.from(episodeReviews)
				.where(episodeReviews.user.id.eq(userId));

		Page<EpisodeReviewResponse> page = PageableExecutionUtils.getPage(reviews, request, countQuery::fetchCount);

		return new PageResponse<>(page);
	}
}
