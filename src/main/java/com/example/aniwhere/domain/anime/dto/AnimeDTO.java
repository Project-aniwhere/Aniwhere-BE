package com.example.aniwhere.domain.anime.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public class AnimeDTO {


    @Getter
    @Setter
    @Builder
    public static class QuarterAnimeResponseDTO {
        private Long animeId;
        private String title;
        private String poster;
        private String weekday;
    }

    @Getter
    @Setter
    @Builder
    public static class AnimeResponseDTO{//애니메이션 개별 반환
        private Long animeId;
        private String title;
        private String director;
        private String characterDesign;
        private String musicDirector;
        private String animationDirector;
        private String script;
        private String producer;
        private String studio;
        private LocalDate releaseDate;
        private LocalDate endDate;
        private Integer episodeNum;
        private String runningTime;
        private String status;
        private String trailer;
        private String description;
        private String poster;
        private Integer airingQuarter;
        private Boolean isAdult;
        private String duration;
        private String weekday;
        private List<RatingDTO> ratings;
        private String backgroundImage;
        private Set<String> categories; //장르

        private List<CastingDTO> castings;  // 등장인물 정보 목록

        private List<EpisodeDTO> episodes;

        private Double averageRating;

        @Getter
        @Setter
        @Builder
        public static class EpisodeDTO {
            private Long episodeId;
            private Integer episodeNumber;
            private String title;
            private LocalDate releaseDate;
            private Integer duration;
            private String episodeStory;
            private String stillImage;

        }

        @Getter
        @Setter
        @Builder
        public static class CastingDTO {
            private Long castingId;
            private String characterName;         // 배역 이름
            private String characterDescription;  // 배역 설명
            private String voiceActorName;        // 성우 이름
        }

        @Getter
        @Setter
        @Builder
        public static class VoiceActorDTO {
            private Long voiceActorId;
            private String name;
        }

        @Getter
        @Setter
        @Builder
        public static class RatingDTO {
            private Long reviewId;         // 리뷰 ID
            private Long animeId;          // 애니메이션 ID
            private String userId;         // 사용자 ID (providerId 형태)
            @NotNull
            @DecimalMin("0.0")
            @DecimalMax("5.0")
            private Double rating;     // 평점
            private String content;        // 리뷰 내용
            private LocalDateTime createdAt; // 리뷰 작성 시간
        }
    }

    @Builder
    @Getter
    @Setter
    public static class WeekdayAnimeDTO { // 요일별 애니메이션 DTO
        private Long animeId;
        private String title;
        private String poster;
        private String weekday;
    }

    @Builder
    @Getter
    @Setter
    public static class AnimeGroupedByWeekdayDTO {
        private Integer weekdayCode;
        private List<WeekdayAnimeDTO> animes;
    }
}
