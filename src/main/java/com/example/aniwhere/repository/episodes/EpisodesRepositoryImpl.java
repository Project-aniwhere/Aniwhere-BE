package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.QEpisodesDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.application.anime.repository.AnimeCustomRepositoryImpl.anime;
import static com.example.aniwhere.domain.episodes.QEpisodes.episodes;

@Repository
@RequiredArgsConstructor
public class EpisodesRepositoryImpl implements EpisodesRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<EpisodesDto> getEpisodes(Long animeId, Pageable pageable) {
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
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.orderBy(episodes.episodeNumber.asc())
				.fetch();

		JPAQuery<Episodes> countQuery = queryFactory
				.select(episodes)
				.from(episodes)
				.leftJoin(episodes.anime, anime)
				.where(episodes.anime.animeId.eq(animeId));

		return PageableExecutionUtils.getPage(episodesList, pageable, countQuery::fetchCount);
	}
}
