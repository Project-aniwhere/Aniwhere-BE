package com.example.aniwhere.domain.category;

import com.example.aniwhere.domain.anime.Anime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "animecategories")
public class AnimeCategory {

    @EmbeddedId
    private AnimeCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("animeId")
    @JoinColumn(name = "anime_id", referencedColumnName = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Category category;
}

