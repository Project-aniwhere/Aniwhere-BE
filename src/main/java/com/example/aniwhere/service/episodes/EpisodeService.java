package com.example.aniwhere.service.episodes;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.UserRepository;
import com.example.aniwhere.repository.episodes.EpisodesRepository;
import com.example.aniwhere.repository.episodesReview.EpisodesReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodeService {

	private final UserRepository userRepository;
	private final EpisodesReviewRepository episodeReviewRepository;
	private final EpisodesRepository episodesRepository;

	@Transactional
	public void addReview(Long episodeId, @RequestBody EpisodeReviewRequest request) {

		Episodes episode = episodesRepository.findById(episodeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE));

		User user = userRepository.findByNickname(request.getNickname())
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		EpisodeReviews episodeReviews = EpisodeReviews.builder()
				.episodes(episode)
				.user(user)
				.rating(request.getRating())
				.content(request.getContent())
				.build();

		episodeReviewRepository.save(episodeReviews);
	}
}
