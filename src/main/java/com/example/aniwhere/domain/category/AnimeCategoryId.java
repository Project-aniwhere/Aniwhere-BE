package com.example.aniwhere.domain.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimeCategoryId implements Serializable {

    @Column(name = "anime_id")
    private Long animeId;

    @Column(name = "category_id")
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimeCategoryId that = (AnimeCategoryId) o;
        return Objects.equals(animeId, that.animeId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(animeId, categoryId);
    }
}
