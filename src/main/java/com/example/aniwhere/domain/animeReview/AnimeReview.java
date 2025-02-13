package com.example.aniwhere.domain.animeReview;


import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnimeReview extends Common {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Lob
    private String content;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public AnimeReview(Long id, String content, Double rating, Anime anime, User user) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.anime = anime;
        this.user = user;
    }

    public void change(double rating, String content){
        this.rating = rating;
        this.content = content;
    }

    public void setAnimeReview(User user) {
        this.user = user;

        if (!user.getAnimeReviews().contains(this)) {
            user.getAnimeReviews().add(this);
        }
    }
}
