package com.example.aniwhere.service.anime.service;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.repository.anime.repository.AnimeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class AnimeRecommender {

    private final AnimeFeatureExtractorService extractorService;
    private final AnimeRepository animeRepository;

    private double[][] featureVectors;
    private Anime[] allAnimesArray;

    @Transactional
    @PostConstruct
    @Scheduled(cron = "0 0 0 1 * *")
    public void initialize() {
        List<Anime> allAnimes = fetchAllAnimesWithCategories();

        this.featureVectors = allAnimes.stream()
                .map(anime -> extractorService.extractFeatures(anime.getAnimeId()))
                .toArray(double[][]::new);

        this.allAnimesArray = allAnimes.toArray(new Anime[0]);
    }

    @Transactional(readOnly = true)
    protected List<Anime> fetchAllAnimesWithCategories() {
        return animeRepository.findAllWithCategories();
    }

    public List<Anime> recommend(List<Anime> userPickedAnimes, int k) { // ✅ List<Anime>으로 변경
        if (featureVectors == null || allAnimesArray == null) {
            throw new IllegalStateException("Recommender is not initialized.");
        }

        KDTree<Anime> kdTree = new KDTree<>(featureVectors, allAnimesArray); // kdTree 생성

        Set<Anime> recommendations = new HashSet<>();
        for (Anime pickedAnime : userPickedAnimes) { // ✅ PickedAnime → Anime으로 변경
            double[] pickedFeatures = extractorService.extractFeatures(pickedAnime.getAnimeId());

            Neighbor<double[], Anime>[] neighbors = kdTree.search(pickedFeatures, k);

            for (Neighbor<double[], Anime> neighbor : neighbors) {
                Anime recommendedAnime = neighbor.value;

                if (userPickedAnimes.stream().noneMatch(p -> p.equals(recommendedAnime))) { // ✅ getAnime() 제거
                    recommendations.add(recommendedAnime);
                }
            }
        }

        List<Anime> resultList = new ArrayList<>(recommendations);
        return resultList.subList(0, Math.min(resultList.size(), 10)); // 최대 10개
    }

}

