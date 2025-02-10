package com.example.aniwhere.repository.history;

import com.example.aniwhere.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

}
