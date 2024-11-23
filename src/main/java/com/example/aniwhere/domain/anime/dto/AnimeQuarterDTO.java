package com.example.aniwhere.domain.anime.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class AnimeQuarterDTO {
    @Getter
    @Setter
    @Builder
    static class AnimeRequestDTO {
        private int year;
        private int quarter;
    }


    @Getter
    @Setter
    @Builder
    public static class AnimeResponseDTO {
        private Long animeId;
        private String title;
        private String poster;
        private String weekday;
    }
}
