package com.example.aniwhere.repository.anime.repository;

import com.example.aniwhere.domain.anime.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  AnimeRepository extends JpaRepository<Anime, Long>, AnimeCustomRepository {
    @Query("SELECT DISTINCT a FROM Anime a LEFT JOIN FETCH a.episodesList LEFT JOIN FETCH a.animeCategories ac LEFT JOIN FETCH ac.category")
    List<Anime> findAllWithCategories();

    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.episodesList WHERE a.animeId = :id")
    Optional<Anime> findByIdWithEpisodes(@Param("id") Long id);

    @Query("SELECT a FROM Anime a WHERE YEAR(a.releaseDate) = :year AND a.airingQuarter = :quarter")
    List<Anime> findByYearAndQuarter(@Param("year") int year, @Param("quarter") int quarter);
}
