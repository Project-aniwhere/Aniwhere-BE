package com.example.aniwhere.domain.division;

import com.example.aniwhere.domain.anime.Anime;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
    private List<DivisionAnime> divisionAnimes = new ArrayList<>();
}