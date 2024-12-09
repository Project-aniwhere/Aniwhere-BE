package com.example.aniwhere.domain.episodes.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodesDto {

	private Long episode_id;
	private Long animeId;
	private Integer episodeNumber;
	private String title;
	private LocalDate releaseDate;
	private Integer duration;
	private String episodeStory;
	private String stillImage;

	@QueryProjection
	public EpisodesDto(Long episode_id, Long animeId, Integer episodeNumber, String title,
					   LocalDate releaseDate, Integer duration, String episodeStory, String stillImage) {
		this.episode_id = episode_id;
		this.animeId = animeId;
		this.episodeNumber = episodeNumber;
		this.title = title;
		this.releaseDate = releaseDate;
		this.duration = duration;
		this.episodeStory = episodeStory;
		this.stillImage = stillImage;
	}

	public static EpisodesDto of(Long episodeId, Long animeId, Integer episodeNumber, String title,
								 LocalDate releaseDate, Integer duration, String episodeStory, String stillImage) {
		return new EpisodesDto(episodeId, animeId, episodeNumber, title, releaseDate, duration, episodeStory, stillImage);
	}
}
