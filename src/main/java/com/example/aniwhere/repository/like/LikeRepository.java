package com.example.aniwhere.repository.like;

import com.example.aniwhere.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUser_Nickname(String nickname);
}
