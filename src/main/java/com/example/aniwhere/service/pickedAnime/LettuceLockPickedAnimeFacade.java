package com.example.aniwhere.service.pickedAnime;

import com.example.aniwhere.repository.pickedAnime.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockPickedAnimeFacade {

	private final RedisLockRepository redisLockRepository;
	private final PickedAnimeService pickedAnimeService;

	public void addAnime(Long animeId, Long userId) throws InterruptedException {
		while (!redisLockRepository.lock(animeId)) {
			Thread.sleep(100);
		}

		try {
			pickedAnimeService.addPickedAnime(animeId, userId);
		} finally {
			redisLockRepository.unlock(animeId);
		}
	}

	public void deleteAnime(Long animeId, Long userId) throws InterruptedException {
		while (!redisLockRepository.lock(animeId)) {
			Thread.sleep(100);
		}

		try {
			pickedAnimeService.deletePickedAnime(animeId, userId);
		} finally {
			redisLockRepository.unlock(animeId);
		}
	}
}
