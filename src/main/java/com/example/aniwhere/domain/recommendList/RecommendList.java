package com.example.aniwhere.domain.recommendList;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class RecommendList extends Common {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "recommendList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommendListAnime> animes = new ArrayList<>();
}