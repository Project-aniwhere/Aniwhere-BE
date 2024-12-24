package com.example.aniwhere.repository.division;

import com.example.aniwhere.domain.division.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, Integer> {
    Optional<Division> findByName(String name);
}
