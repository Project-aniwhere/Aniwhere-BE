package com.example.aniwhere.repository.user;

import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByNickname(String nickname);
	boolean existsByEmail(String email);
}
