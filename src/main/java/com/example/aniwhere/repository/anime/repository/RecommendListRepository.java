package com.example.aniwhere.repository.anime.repository;

import com.example.aniwhere.domain.recommendList.RecommendList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendListRepository extends JpaRepository<RecommendList, Long> {
}
