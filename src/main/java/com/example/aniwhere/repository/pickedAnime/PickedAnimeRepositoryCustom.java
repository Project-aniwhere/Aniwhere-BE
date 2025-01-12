package com.example.aniwhere.repository.pickedAnime;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.pickedAnime.dto.PickedAnimeDto;
import org.springframework.stereotype.Repository;

@Repository
public interface PickedAnimeRepositoryCustom {
	PageResponse<PickedAnimeDto> getPickedAnime(Long userId, PageRequest pageRequest);
}
