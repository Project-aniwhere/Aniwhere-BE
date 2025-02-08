package com.example.aniwhere.repository.user;

import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.role = :role")
	List<User> findAllByRole(@Param("role") Role role);

	boolean existsByNickname(String nickname);
	boolean existsByEmail(String email);

	Optional<User> findByNickname(String nickname);
}
