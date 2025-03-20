package com.example.aniwhere.domain.anime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "anime_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnimeKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @ManyToOne
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @Column(nullable = false)
    private String keyword;
}
