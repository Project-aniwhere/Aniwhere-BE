package com.example.aniwhere.domain.episodes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EpisodesInfoDto {

	private Long episodeId;
	private int episodeNumber;
	private String title;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate releaseDate;
	private int duration;
	private String episodeStory;
	private String stillImage;
	private double averageRating;
	private List<ReviewDto> reviews = new ArrayList<>();

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReviewDto {
		private Long episodeReviewId;
		private Long episodeId;
		private Long userId;
		private double rating;
		private String content;
		private String nickname;
	}
}
