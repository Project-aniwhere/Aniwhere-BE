package com.example.aniwhere.application.anime.service;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.division.Division;
import com.example.aniwhere.domain.division.DivisionRepository;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smile.clustering.DBSCAN;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.aniwhere.domain.user.Sex.male;

@Service
@RequiredArgsConstructor
public class ClusteringService {

    private final UserRepository userRepository;
    private final DivisionRepository divisionRepository;

    public void clusterAndSave() {
        //사용자 데이터 로드
        List<User> users = userRepository.findAll();

        int currentYear = LocalDate.now().getYear();

        // 사용자 데이터 그룹화 준비(성별과 나이)
        double[][] userData = users.stream()
                .map(user -> {
                    int age = currentYear - Integer.parseInt(user.getBirthyear()); // 나이 계산
                    double genderCode = user.getSex().equals(male) ? 0.0 : 1.0; // 성별 코드
                    return new double[]{age, genderCode};
                })
                .toArray(double[][]::new);

        //minpts : 반경내에 점 몇개?, radius : 반경
        DBSCAN<double[]> dbscan = DBSCAN.fit(userData, 2, 5);

        // 그룹화
        Map<Integer, List<User>> clusters = new HashMap<>();
        for (int i = 0; i < dbscan.y.length; i++) {
            clusters.computeIfAbsent(dbscan.y[i], k -> new ArrayList<>()).add(users.get(i));
        }

        // 각 그룹별 선호 애니메이션 추출 및 저장.
        for (Map.Entry<Integer, List<User>> entry : clusters.entrySet()) {
            List<User> clusterUsers = entry.getValue();

            String groupName = generateGroupName(clusterUsers);

            // 상위 20개 애니메이션 추출
            List<Anime> topAnimes = getTopAnimes(clusterUsers, 20);

            // Division에 저장
            saveDivision(groupName, topAnimes);
        }
    }
    //성별과 나이에서 그룹이름 뽑아내기
    private String generateGroupName(List<User> users) {
        int currentYear = LocalDate.now().getYear();

        int avgAge = (int) users.stream()
                .mapToInt(user -> currentYear - Integer.parseInt(user.getBirthyear()))
                .average()
                .orElse(0);
        String gender = users.get(0).getSex()==male ? "Male" : "Female" ;
        return gender + (avgAge / 10 * 10); // "Male20" or "Female30"
    }

    private List<Anime> getTopAnimes(List<User> users, int limit) {//선호애니메이션 list가져와서 20개만큼 뽑기
        Map<Anime, Long> animeFrequency = new HashMap<>();
//        for (User user : users) {
//            for (Anime anime : user.getFavoriteAnimes()) {
//                animeFrequency.put(anime, animeFrequency.getOrDefault(anime, 0L) + 1);
//            }
//        }

        return animeFrequency.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    //결과 저장.
    private void saveDivision(String groupName, List<Anime> animes) {
        Division division = divisionRepository.findByName(groupName).get();
        division.setName(groupName);
        division.setLastUpdated(LocalDate.now());
        division.getDivisionAnimes().clear();
        division.getDivisionAnimes().addAll(animes);
        divisionRepository.save(division);
    }
}
