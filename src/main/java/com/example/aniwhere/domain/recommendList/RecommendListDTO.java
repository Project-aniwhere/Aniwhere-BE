package com.example.aniwhere.domain.recommendList;

import com.example.aniwhere.domain.anime.dto.AnimeSummaryDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecommendListDTO {
    private Long id;                          // 추천 리스트 ID
    private String title;                     // 추천 리스트 제목
    private String description;               // 추천 리스트 설명
    private List<AnimeSummaryDTO> animes;     // 애니메이션 요약 리스트
}
