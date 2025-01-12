package com.example.aniwhere.service.division;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import com.example.aniwhere.domain.division.Division;
import com.example.aniwhere.domain.like.Like;
import com.example.aniwhere.domain.review.Review;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.repository.division.DivisionRepository;
import com.example.aniwhere.repository.like.LikeRepository;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionService {
    private final DivisionRepository divisionRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    @Transactional
    public void updateAnimeLikesByDivision() {
        List<Division> divisions = divisionRepository.findAll();

        //기존 데이터 초기화
        divisions.forEach(division -> division.getDivisionAnimes().clear());

        List<Like> likes = likeRepository.findAll();

        Map<String, List<Anime>> groupedAnimes = likes.stream()
                .collect(Collectors.groupingBy(
                        like -> getDivisionName(like.getUser().getSex().name(), calculateAge(like.getUser().getBirthyear())),
                        Collectors.mapping(Like::getAnime, Collectors.toList())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().limit(20).collect(Collectors.toList()) // 상위 20개만 선택
                ));


        Map<String, Division> divisionMap = divisions.stream()
                .collect(Collectors.toMap(Division::getName, division -> division));

        for (String divisionName : groupedAnimes.keySet()) {
            Division division = divisionMap.getOrDefault(divisionName, new Division());
            division.setName(divisionName);
            division.setDivisionAnimes(groupedAnimes.get(divisionName));
            divisionRepository.save(division);
        }
    }

    @Transactional(readOnly = true)
    public List<AnimeSummaryDTO> recommendAnimes(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("User not found: " + nickname));

        String gender = user.getSex().name(); //성별
        int age = LocalDate.now().getYear() - Integer.parseInt(user.getBirthyear()); //나이

        String divisionName = getDivisionName(gender, age);//그룹 이름

        Division division = divisionRepository.findByName(divisionName)
                .orElseThrow(() -> new RuntimeException("Division not found: " + divisionName));

        return division.getDivisionAnimes().stream()
                .map(anime -> AnimeSummaryDTO.builder()
                        .animeId(anime.getAnimeId())
                        .title(anime.getTitle())
                        .poster(anime.getPoster())
                        .averageRating(calculateAverageRating(anime.getReviews()))
                        .build())
                .collect(Collectors.toList());
    }

    private Double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0; // 리뷰가 없으면 0.0 반환
        }
        return reviews.stream()
                .mapToDouble(review -> review.getRating().doubleValue()) // 변환 추가
                .average()
                .orElse(0.0);
    }


    private int calculateAge(String birthYear) {
        int currentYear = LocalDate.now().getYear();
        return currentYear - Integer.parseInt(birthYear);
    }

    private String getDivisionName(String gender, int age) {
        if (age >= 10 && age < 20) {
            return gender.equalsIgnoreCase("male") ? "Male10" : "Female10";
        } else if (age >= 20 && age < 30) {
            return gender.equalsIgnoreCase("male") ? "Male20" : "Female20";
        } else if (age >= 30 && age < 40) {
            return gender.equalsIgnoreCase("male") ? "Male30" : "Female30";
        } else if (age >= 40) {
            return gender.equalsIgnoreCase("male") ? "Male40" : "Female40";
        } else {
            throw new IllegalArgumentException("Invalid age: " + age);
        }
    }
}
