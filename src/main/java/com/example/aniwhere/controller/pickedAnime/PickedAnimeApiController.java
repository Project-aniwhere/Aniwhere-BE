package com.example.aniwhere.controller.pickedAnime;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.pickedAnime.dto.PickedAnimeDto;
import com.example.aniwhere.repository.pickedAnime.PickedAnimeRepository;
import com.example.aniwhere.service.pickedAnime.LettuceLockPickedAnimeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PickedAnimeApiController {

	private final PickedAnimeRepository pickedAnimeRepository;
	private final LettuceLockPickedAnimeFacade lettuceLockPickedAnimeFacade;

	@GetMapping("/pick")
	public ResponseEntity<PageResponse<PickedAnimeDto>> getPickedAnime(@LoginUser final Long userId,
																	   final PageRequest pageRequest) {
		PageResponse<PickedAnimeDto> pickedAnime = pickedAnimeRepository.getPickedAnime(userId, pageRequest);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(pickedAnime);
	}

	@PostMapping("/pick/{animeId}")
	public ResponseEntity<Void> addPickedAnime(@PathVariable(name = "animeId") final Long animeId,
											   @LoginUser final Long userId) throws InterruptedException {
		lettuceLockPickedAnimeFacade.addAnime(animeId, userId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}

	@DeleteMapping("/pick/{animeId}")
	public ResponseEntity<Void> deletePickedAnime(@PathVariable(name = "animeId") final Long animeId,
												  @LoginUser final Long userId) throws InterruptedException {
		lettuceLockPickedAnimeFacade.deleteAnime(animeId, userId);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}
}
