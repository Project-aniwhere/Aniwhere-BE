package com.example.aniwhere.application.anime.service;

import com.example.aniwhere.application.anime.repository.RecommendListRepository;
import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.division.Division;
import com.example.aniwhere.domain.division.DivisionRepository;
import com.example.aniwhere.domain.recommendList.RecommendList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendListRepository recommendListRepository;
    private final DivisionRepository divisionRepository;


    public List<RecommendList> getRecommendLists() {
        return recommendListRepository.findAll();
    }

    public RecommendList insertRecommendList(RecommendList recommendList) {
        return recommendListRepository.save(recommendList);
    }

    public void deleteRecommendList(long id) {
        recommendListRepository.deleteById(id);
    }

    public RecommendList updateRecommendList(Long id, RecommendList recommendList) {
        recommendList.setId(id);
        return recommendListRepository.save(recommendList);
    }

    @Transactional(readOnly = true)
    public List<Anime> recommendAnimes(String gender, int age) {
        // 1. Division 이름 결정
        String divisionName = getDivisionName(gender, age);

        // 2. Division 가져오기
        Division division = divisionRepository.findByName(divisionName)
                .orElseThrow(() -> new RuntimeException("Division not found: " + divisionName));

        // 3. 연결된 Anime 리스트 반환
        return division.getDivisionAnimes();
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
