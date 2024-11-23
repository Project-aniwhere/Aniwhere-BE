package com.example.aniwhere.domain.division;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionService {
    private final DivisionRepository divisionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void populateDivisions() {
        List<String> divisionNames = Arrays.asList(
                "Male10", "Female10",
                "Male20", "Female20",
                "Male30", "Female30",
                "Male40", "Female40"
        );

        for (String name : divisionNames) {
            Division division = divisionRepository.findByName(name)
                    .orElseGet(() -> new Division());
            division.setName(name);
            division.setLastUpdated(LocalDate.now());
            divisionRepository.save(division);
        }
    }
}
