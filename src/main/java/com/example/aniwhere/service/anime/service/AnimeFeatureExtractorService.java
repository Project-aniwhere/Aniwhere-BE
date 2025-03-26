package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.category.AnimeCategory;
import com.example.aniwhere.domain.category.Category;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import com.example.aniwhere.repository.category.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class AnimeFeatureExtractorService {

    private final CategoryRepository categoryRepository;
    private Map<String, Integer> genreIndexMap;

    @PostConstruct
    public void init() {
        // 모든 카테고리 이름을 가져온 후 중복 제거 및 인덱스 맵 생성
        List<String> allGenres = categoryRepository.findAllCategoryNames()
                .stream()
                .distinct()
                .toList();

        genreIndexMap = new HashMap<>();
        for (int i = 0; i < allGenres.size(); i++) {
            genreIndexMap.put(allGenres.get(i), i);
        }
    }

    // 애니메이션 객체를 받아서 feature 벡터를 추출하는 메서드
    @Transactional(readOnly = true)
    public double[] extractFeatures(Anime anime) {
        double[] genreFeatures = encodeCategories(
                anime.getAnimeCategories().stream()
                        .map(AnimeCategory::getCategory)
                        .collect(Collectors.toSet())
        );

        double studioFeature = encodeStudio(anime.getStudio());
        double isAdultFeature = anime.getIsAdult() != null && anime.getIsAdult() ? 1.0 : 0.0;

        return concat(genreFeatures, new double[]{studioFeature, isAdultFeature});
    }

    private double[] encodeCategories(Set<Category> categories) {
        double[] genreVector = new double[genreIndexMap.size()];

        for (Category category : categories) {
            Integer index = genreIndexMap.get(category.getCategoryName());
            if (index != null) {
                genreVector[index] = 1.0;
            }
        }
        return genreVector;
    }

    private double encodeStudio(String studio) {
        return studio != null ? studio.hashCode() % 1000 : 0.0;
    }

    private double[] concat(double[]... arrays) {
        int totalLength = 0;
        for (double[] array : arrays) {
            totalLength += array.length;
        }

        double[] result = new double[totalLength];
        int index = 0;
        for (double[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }
        return result;
    }
}
