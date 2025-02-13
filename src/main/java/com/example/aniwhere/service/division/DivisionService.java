package com.example.aniwhere.service.division;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import com.example.aniwhere.domain.division.Division;
import com.example.aniwhere.domain.division.DivisionAnime;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.domain.rating.Rating;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.division.DivisionRepository;
import com.example.aniwhere.repository.pickedAnime.PickedAnimeRepository;
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

import static com.example.aniwhere.global.error.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionService {
    private final DivisionRepository divisionRepository;
    private final PickedAnimeRepository pickedAnimeRepository; // 기존 LikeRepository 대신 PickedAnimeRepository 사용
    private final UserRepository userRepository;

    /**
     * 매일 자정에 실행 - 각 연령/성별 그룹별 인기 애니 업데이트
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateAnimeLikesByDivision() {
        List<Division> divisions = divisionRepository.findAll();

        divisions.forEach(division -> division.getDivisionAnimes().clear());

        List<PickedAnime> pickedAnimes = pickedAnimeRepository.findAll(); // PickedAnime 사용

        Map<String, List<Anime>> groupedAnimes = pickedAnimes.stream()
                .collect(Collectors.groupingBy(
                        pickedAnime -> getDivisionName(pickedAnime.getUser().getSex().name(),
                                calculateAge(pickedAnime.getUser().getBirthyear())),
                        Collectors.mapping(PickedAnime::getAnime, Collectors.toList())
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

            division.getDivisionAnimes().clear();

            List<DivisionAnime> divisionAnimeList = groupedAnimes.get(divisionName).stream()
                    .map(anime -> DivisionAnime.builder()
                            .division(division)
                            .anime(anime)
                            .build())
                    .collect(Collectors.toList());

            division.getDivisionAnimes().addAll(divisionAnimeList);

            divisionRepository.save(division);
        }

    }

    /**
     * 특정 유저의 성별/연령대 기반 추천 애니메이션 반환
     */
    @Transactional(readOnly = true)
    public List<AnimeSummaryDTO> recommendAnimes(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        String gender = user.getSex().name(); // 성별
        int age = calculateAge(user.getBirthyear()); // 나이 계산

        String divisionName = getDivisionName(gender, age); // 그룹 이름

        Division division = divisionRepository.findByName(divisionName)
                .orElseThrow(() -> new RuntimeException("Division not found: " + divisionName));

        return division.getDivisionAnimes().stream()
                .map(divisionAnime -> {
                    Anime anime = divisionAnime.getAnime();
                    return AnimeSummaryDTO.builder()
                            .animeId(anime.getAnimeId())
                            .title(anime.getTitle())
                            .poster(anime.getPoster())
                            .averageRating(calculateAverageRating(anime.getRatings()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 애니메이션 평균 평점 계산
     */
    private Double calculateAverageRating(List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0; // 리뷰가 없으면 0.0 반환
        }
        return ratings.stream()
                .mapToDouble(review -> review.getRating().doubleValue()) // 변환 추가
                .average()
                .orElse(0.0);
    }

    /**
     * 출생 연도를 기반으로 나이 계산
     */
    private int calculateAge(String birthYear) {
        int currentYear = LocalDate.now().getYear();
        return currentYear - Integer.parseInt(birthYear);
    }

    /**
     * 성별과 나이를 기반으로 그룹 이름 반환
     */
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
