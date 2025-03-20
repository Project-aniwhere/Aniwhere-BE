package com.example.aniwhere.domain.anime.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnimeSearchResponseDto {
    private Long animeId;
    private String title;
    private String status;
    private String poster;

    @QueryProjection
    public AnimeSearchResponseDto(Long animeId, String title, String status, String poster) {
        this.animeId = animeId;
        this.title = title;
        this.status = status;
        this.poster = poster;
    }
}
