package com.example.aniwhere.repository.anime.repository;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AnimeCustomRepository {

    List<AnimeDTO.AnimeGroupedByWeekdayDTO> findAllGroupedByWeekday(Integer year, Integer quarter);
}
