package com.example.aniwhere.service.episodes;

import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.QEpisodesDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.aniwhere.domain.episodes.QEpisodes.episodes;
import static com.example.aniwhere.domain.anime.QAnime.anime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodesService {

	private final JPAQueryFactory queryFactory;

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
