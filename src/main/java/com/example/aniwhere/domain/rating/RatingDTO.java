package com.example.aniwhere.domain.rating;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Builder
@Setter
public class RatingDTO {
    private Long userId;
    private Long animeId;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;
}
