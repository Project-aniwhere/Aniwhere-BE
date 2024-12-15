package com.example.aniwhere.repository.episodes;

import com.example.aniwhere.domain.episodes.Episodes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodesRepository extends JpaRepository<Episodes, Long>, EpisodesRepositoryCustom {

}
