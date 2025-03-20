package com.example.aniwhere.repository.anime.repository;

import com.example.aniwhere.domain.anime.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT DISTINCT a FROM Anime a " +
            "LEFT JOIN FETCH a.episodesList " +
            "LEFT JOIN FETCH a.animeCategories ac " +
            "LEFT JOIN FETCH ac.category " +
            "WHERE a.animeId = :id")
    Optional<Anime> findByIdWithEpisodes(@Param("id") Long id);

    @Query("SELECT a FROM Anime a WHERE YEAR(a.releaseDate) = :year AND a.airingQuarter = :quarter")
    List<Anime> findByYearAndQuarter(@Param("year") int year, @Param("quarter") int quarter);

@Query(value = """
WITH CurrentQuarter AS (
    SELECT
        YEAR(NOW()) AS current_year,
        CASE
            WHEN MONTH(NOW()) BETWEEN 1 AND 3 THEN 1
            WHEN MONTH(NOW()) BETWEEN 4 AND 6 THEN 2
            WHEN MONTH(NOW()) BETWEEN 7 AND 9 THEN 3
            ELSE 4
        END AS current_quarter
)
SELECT
    a.anime_id AS animeId,
    a.title AS title,
    a.description AS description,
    a.poster AS poster,
    a.studio AS studio,
    a.episodes AS episodes,
    COALESCE((SELECT AVG(r.rating) FROM review r WHERE r.anime_id = a.anime_id), 0) AS averageRating,
    (SELECT r.content FROM review r WHERE r.anime_id = a.anime_id ORDER BY r.created_at DESC LIMIT 1) AS latestReview
FROM anime a
JOIN picked_anime p ON a.anime_id = p.anime_id
JOIN CurrentQuarter cq ON a.airing_quarter = cq.current_quarter
AND YEAR(a.release_date) = cq.current_year
GROUP BY a.anime_id
ORDER BY COUNT(p.id) DESC
LIMIT :limit
""", nativeQuery = true)
List<Object[]> findPopularAnime(@Param("limit") int limit);
}
