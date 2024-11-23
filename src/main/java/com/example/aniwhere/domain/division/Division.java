package com.example.aniwhere.domain.division;

import com.example.aniwhere.domain.anime.Anime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated;

    @ManyToMany
    @JoinTable(
            name = "division_anime", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "group_id"), // division 테이블의 외래 키
            inverseJoinColumns = @JoinColumn(name = "anime_id") // anime 테이블의 외래 키
    )
    private List<Anime> divisionAnimes = new ArrayList<>();


}