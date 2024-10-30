package com.example.aniwhere.domain.anime.service;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeQuarterDTO.*;
import com.example.aniwhere.domain.anime.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;

    public Map<String, List<AnimeResponseDTO>> getAnimeByYearAndQuarter(int year, int quarter) {
        List<Anime> animes = animeRepository.findByYearAndQuarter(year, quarter);


        List<String> weekdays = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일");

        // 요일별로 response
        Map<String, List<AnimeResponseDTO>> groupedAnimes = weekdays.stream()
                .collect(Collectors.toMap(day -> day, day -> new ArrayList<>(), (a, b) -> a, LinkedHashMap::new));

        // 애니메이션 요일별로
        animes.stream()
                .filter(anime -> weekdays.contains(anime.getWeekday())) // 요일이 없는 경우 고려
                .map(this::convertToDTO)
                .forEach(anime -> groupedAnimes.get(anime.getWeekday()).add(anime));

        return groupedAnimes;
    }

    //Anime response로 변환
    private AnimeResponseDTO convertToDTO(Anime anime) {
        return AnimeResponseDTO.builder()
                .animeId(anime.getAnimeId())
                .title(anime.getTitle())
                .poster(anime.getPoster())
                .weekday(anime.getWeekday())
                .build();
    }
}
