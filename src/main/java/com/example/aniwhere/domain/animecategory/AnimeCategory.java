package com.example.aniwhere.domain.animecategory;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.catogory.Category;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@Table(name = "animecategories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnimeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}