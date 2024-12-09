package com.example.aniwhere.application.anime.repository;

import com.example.aniwhere.domain.anime.Anime;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AnimeCustomRepository {

    Map<Integer, List<Anime>> findAllGroupedByWeekday();
}
