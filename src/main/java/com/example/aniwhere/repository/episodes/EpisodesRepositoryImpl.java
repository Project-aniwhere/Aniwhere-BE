package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.QEpisodesDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
