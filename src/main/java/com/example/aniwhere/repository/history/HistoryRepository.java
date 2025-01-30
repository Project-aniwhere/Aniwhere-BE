package com.example.aniwhere.repository.history;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.Status;
import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

	List<History> findHistoriesByStatusAndSender(Status status, User sender);
}
