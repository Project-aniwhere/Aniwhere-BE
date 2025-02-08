package com.example.aniwhere.controller.anime.rating;

import com.example.aniwhere.domain.rating.RatingDTO;
import com.example.aniwhere.service.rating.RatingService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> submitRating(@RequestBody RatingDTO ratingDTO) {
        ratingService.submitRating(ratingDTO);
        return ResponseEntity.ok("Rating submitted successfully");
    }

    @GetMapping
    public ResponseEntity<Double> getUserRating(
            @RequestParam Long userId,
            @RequestParam Long animeId
    ) {
        Double rating = ratingService.getUserRating(userId, animeId);
        return ResponseEntity.ok(rating);
    }
}
