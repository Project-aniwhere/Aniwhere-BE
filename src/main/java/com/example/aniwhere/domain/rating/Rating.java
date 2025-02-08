package com.example.aniwhere.domain.rating;


import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "rating")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating extends Common {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score", precision = 10)
    private Double rating;

    public void setReview(User user) {
        this.user = user;

        // 무한 루프 방지
        if (!user.getAnimeReviews().contains(this)) {
            user.getAnimeReviews().add(this);
        }
    }
}