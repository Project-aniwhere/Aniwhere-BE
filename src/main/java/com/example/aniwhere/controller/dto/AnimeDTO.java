package com.example.aniwhere.controller.dto;


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
    public static class QuarterAnimeResponseDTO {//분기별 애니메이션 반환
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
        private Integer episodes;
        private String runningTime;
        private String status;
        private String trailer;
        private String description;
        private String poster;
        private Integer airingQuarter;
        private Boolean isAdult;
        private String duration;
        private String weekday;
        private String anilistId;
        private Set<String> categories;

        private List<CastingDTO> castings;  // 캐스팅 정보 목록
        private List<ReviewDTO> reviews;    // 리뷰 목록

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
        public static class ReviewDTO {
            private Long reviewId;
            private String userId;               // 작성자 ID
            private BigDecimal rating;           // 평점
            private String content;              // 리뷰 내용
            private LocalDateTime createdAt;     // 리뷰 작성 시간
        }

        @Getter
        @Setter
        @Builder
        public static class VoiceActorDTO {
            private Long voiceActorId;
            private String name;                 // 성우 이름
        }
    }
}
