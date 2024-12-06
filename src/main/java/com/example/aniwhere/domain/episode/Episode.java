package com.example.aniwhere.domain.episode;

import com.example.aniwhere.domain.anime.Anime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "episodes")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    private Integer episodeNumber;

    private String title;

    private LocalDate releaseDate;

    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String episodeStory;

    @Column(columnDefinition = "TEXT")
    private String stillImage;
}