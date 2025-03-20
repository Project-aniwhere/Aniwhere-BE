package com.example.aniwhere.domain.anime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnimeSearchRequestDto {
    private List<String> categories;
    private List<Integer> quarters;
    private String title;
    private List<String> statuses;
    private Integer year;
    private int page = 0;
    private int size = 10;

    public PageRequest toPageRequest() {
        return PageRequest.of(page, size);
    }
}

