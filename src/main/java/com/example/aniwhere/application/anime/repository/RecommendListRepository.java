package com.example.aniwhere.application.anime.repository;

import com.example.aniwhere.domain.recommendList.RecommendList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendListRepository extends JpaRepository<RecommendList, Integer> {
}
