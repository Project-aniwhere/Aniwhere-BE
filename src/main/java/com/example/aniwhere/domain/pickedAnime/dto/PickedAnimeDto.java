package com.example.aniwhere.domain.pickedAnime.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PickedAnimeDto {

	private Long anime_id;
	private String title;
	private String poster;
	private Long user_id;

	@QueryProjection
	public PickedAnimeDto(Long anime_id, Long user_id, String title, String poster) {
		this.anime_id = anime_id;
        this.user_id = user_id;
        this.title = title;
        this.poster = poster;
	}

	public static PickedAnimeDto of(Long anime_id, Long user_id, String title, String poster) {
        return new PickedAnimeDto(anime_id, user_id, title, poster);
    }
}
