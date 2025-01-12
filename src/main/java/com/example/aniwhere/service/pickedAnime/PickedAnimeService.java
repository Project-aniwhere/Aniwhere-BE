package com.example.aniwhere.service.pickedAnime;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.ResourceNotFoundException;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.pickedAnime.PickedAnimeRepository;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PickedAnimeService {

	private final UserRepository userRepository;
	private final AnimeRepository animeRepository;
	private final PickedAnimeRepository pickedAnimeRepository;

	@Transactional
	public void addPickedAnime(Long animeId, Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		Anime anime = animeRepository.findById(animeId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANIME));

		PickedAnime pickedAnime = PickedAnime.builder()
				.anime(anime)
				.user(user)
				.build();
		pickedAnimeRepository.save(pickedAnime);
	}

	@Transactional
	public void deletePickedAnime(Long animeId, Long userId) {

		PickedAnime pickedAnime = pickedAnimeRepository.findByAnime_AnimeIdAndUserId(animeId, userId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_PICKED_ANIME));

		pickedAnimeRepository.delete(pickedAnime);
	}
}
