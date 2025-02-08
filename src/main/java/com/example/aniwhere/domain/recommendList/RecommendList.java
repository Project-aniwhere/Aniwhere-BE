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

    @ManyToMany
    @JoinTable(
            name = "recommend_list_anime",
            joinColumns = @JoinColumn(name = "recommend_id"),
            inverseJoinColumns = @JoinColumn(name = "anime_id")
    )
    private List<Anime> animes = new ArrayList<>();
}