package com.example.aniwhere.application.anime.repository;

import com.example.aniwhere.domain.anime.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  AnimeRepository extends JpaRepository<Anime, Long>, AnimeCustomRepository {
    //DB에 year가 없어서 releaseDate에서 변환
    @Query("SELECT a FROM Anime a WHERE YEAR(a.releaseDate) = :year AND a.airingQuarter = :quarter")
    List<Anime> findByYearAndQuarter(@Param("year") int year, @Param("quarter") int quarter);

}
