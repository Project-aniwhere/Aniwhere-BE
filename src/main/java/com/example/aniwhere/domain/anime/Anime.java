package com.example.aniwhere.domain.anime;

import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.casting.Casting;
import com.example.aniwhere.domain.category.AnimeCategory;
import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.rating.Rating;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "anime")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anime_id")
    private Long animeId;

    private String title;
    private String director;
    private String characterDesign;
    private String musicDirector;
    private String animationDirector;
    private String script;
    private String producer;
    private String studio;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String runningTime;
    private String status;
    private String trailer;
    private String description;
    private String poster;
    private Integer airingQuarter;
    private Boolean isAdult;
    private String duration;
    private String weekday;
    private String backgroundImage;
    private Integer scoreCnt;
    private double totalScore;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Casting> castings = new ArrayList<>();

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimeReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnimeCategory> animeCategories = new HashSet<>();

    @OneToMany(mappedBy = "anime")
    private final List<Episodes> episodesList = new ArrayList<>();

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimeKeyword> keywords = new ArrayList<>();

    public void addEpisodes(Episodes episodes) {
        this.episodesList.add(episodes);
        if (episodes.getAnime() != this) {
            episodes.registerAnime(this);
        }
    }

    public void addReview(double rating){
        this.scoreCnt++;
        this.totalScore+=rating;
    }

    public void updateReview(Double oldRating, double newRating) {
        this.totalScore-=oldRating;
        this.totalScore+=newRating;
    }

    public void deleteAnimeReview(Double rating) {
        this.scoreCnt++;
        this.totalScore-=rating;
    }
}
