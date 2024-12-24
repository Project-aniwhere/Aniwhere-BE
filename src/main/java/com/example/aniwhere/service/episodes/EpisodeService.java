package com.example.aniwhere.service.episodes;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.repository.episodes.EpisodesRepository;
import com.example.aniwhere.repository.episodesReview.EpisodesReviewRepository;
import com.example.aniwhere.service.episodes.dto.EpisodeReviewCommand;
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
	public void addReview(Long episodeId, EpisodeReviewCommand command) {

		Episodes episode = episodesRepository.findById(episodeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE));

		User user = userRepository.findById(command.userId())
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

		checkDuplicateEpisodesReview(episode, user);

		EpisodeReviews episodeReviews = EpisodeReviews.builder()
				.episodes(episode)
				.user(user)
				.rating(command.request().rating())
				.content(command.request().content())
				.build();

		episodeReviewRepository.save(episodeReviews);
	}

	@Transactional
	public void updateReview(Long episodeId, EpisodeReviewCommand command) {
		Episodes episode = episodesRepository.findById(episodeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE));

		User user = userRepository.findById(command.userId())
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

		EpisodeReviews review = episodeReviewRepository.findByEpisodesAndUser(episode, user)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE_REVIEW));

		review.updateReview(command.request().rating(), command.request().content());
	}

	@Transactional
	public void deleteReview(Long episodeId, Long userId) {
		Episodes episode = episodesRepository.findById(episodeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));

		EpisodeReviews review = episodeReviewRepository.findByEpisodesAndUser(episode, user)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EPISODE_REVIEW));

		episodeReviewRepository.delete(review);
		episode.getEpisodeReviews().remove(review);
		episode.updateAverageRating();
	}

	private void checkDuplicateEpisodesReview(Episodes episodes, User user) {
		if (episodeReviewRepository.existsByEpisodesAndUser(episodes, user)) {
			throw new UserException(ALREADY_EXIST_EPISODE_REVIEW);
		}
	}
}
