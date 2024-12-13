package com.example.aniwhere.service.episodes;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import com.example.aniwhere.domain.episodeReviews.dto.QEpisodeReviewResponse;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.episodes.dto.EpisodesDto;
import com.example.aniwhere.domain.episodes.dto.QEpisodesDto;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.UserRepository;
import com.example.aniwhere.repository.episodes.EpisodesRepository;
import com.example.aniwhere.repository.episodesReview.EpisodeReviewRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.example.aniwhere.domain.episodeReviews.QEpisodeReviews.episodeReviews;
import static com.example.aniwhere.domain.episodes.QEpisodes.episodes;
import static com.example.aniwhere.domain.anime.QAnime.anime;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodeService {

	private final JPAQueryFactory queryFactory;
	private final UserRepository userRepository;
	private final EpisodeReviewRepository episodeReviewRepository;
	private final EpisodesRepository episodesRepository;

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

	@Transactional
	public void addReview(Long episodeId, @RequestBody EpisodeReviewRequest request) {

		Episodes episode = episodesRepository.findById(episodeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE));

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		EpisodeReviews episodeReviews = EpisodeReviews.builder()
				.episodes(episode)
				.user(user)
				.rating(request.getRating())
				.content(request.getContent())
				.build();

		episodeReviewRepository.save(episodeReviews);
	}

	public Page<EpisodeReviewResponse> getEpisodeReviews(Long episodeId, Pageable pageable) {
		List<EpisodeReviewResponse> reviews = queryFactory
				.select(new QEpisodeReviewResponse(
						episodeReviews.id,
						episodeReviews.rating,
						episodeReviews.content,
						episodeReviews.user.id
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
}
