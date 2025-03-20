package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.EpisodesInfoDto;
import com.example.aniwhere.domain.episodes.dto.QEpisodesDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.aniwhere.domain.episodeReviews.QEpisodeReviews.episodeReviews;
import static com.example.aniwhere.domain.user.QUser.user;
import static com.example.aniwhere.repository.anime.repository.AnimeCustomRepositoryImpl.anime;
import static com.example.aniwhere.domain.episodes.QEpisodes.episodes;

@Repository
@RequiredArgsConstructor
public class EpisodesRepositoryImpl implements EpisodesRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public PageResponse<EpisodesDto> getEpisodes(Long animeId, PageRequest pageRequest) {

		org.springframework.data.domain.PageRequest request = pageRequest.of();

		List<EpisodesDto> episodesList = queryFactory
				.select(new QEpisodesDto(
						episodes.id,
						episodes.anime.animeId,
						episodes.episodeNumber,
						episodes.title,
						episodes.releaseDate,
						episodes.duration,
						episodes.episodeStory,
						episodes.stillImage
				))
				.from(episodes)
				.where(episodes.anime.animeId.eq(animeId))
				.offset(request.getOffset())
				.limit(request.getPageSize())
				.orderBy(episodes.episodeNumber.asc())
				.fetch();

		JPAQuery<Episodes> countQuery = queryFactory
				.select(episodes)
				.from(episodes)
				.leftJoin(episodes.anime, anime)
				.where(episodes.anime.animeId.eq(animeId));

		Page<EpisodesDto> page = PageableExecutionUtils.getPage(episodesList, request, countQuery::fetchCount);

		return new PageResponse<>(page);
	}

	@Override
	public List<EpisodesInfoDto> getEpisodeById(Long episodeId) {

		EpisodesInfoDto episodeInfo = queryFactory
				.select(Projections.bean(EpisodesInfoDto.class,
						episodes.id.as("episodeId"),
						episodes.episodeNumber,
						episodes.title,
						episodes.releaseDate,
						episodes.duration,
						episodes.episodeStory,
						episodes.stillImage,
						episodes.averageRating
				))
				.from(episodes)
				.where(episodes.id.eq(episodeId))
				.fetchOne();

		if (episodeInfo != null) {
			List<EpisodesInfoDto.ReviewDto> reviews = queryFactory
					.select(Projections.bean(EpisodesInfoDto.ReviewDto.class,
							episodeReviews.id.as("episodeReviewId"),
							episodeReviews.episodes.id.as("episodeId"),
							episodeReviews.user.id.as("userId"),
							episodeReviews.rating,
							episodeReviews.content,
							user.nickname
					))
					.from(episodeReviews)
					.join(episodeReviews.user, user)
					.join(episodeReviews.episodes, episodes)
					.where(episodeReviews.episodes.id.eq(episodeId))
					.fetch();
			episodeInfo.setReviews(reviews != null ? reviews : new ArrayList<>());
			return List.of(episodeInfo);
		}
		return List.of();
	}
}
