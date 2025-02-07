package com.example.aniwhere.domain.pickedAnime;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PickedAnime extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pickedAnime_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "anime_id")
	private Anime anime;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public void pickedAnime(User user) {
		this.user = user;

		// 무한 루프 방지
		if (!user.getPickedAnimes().contains(this)) {
			user.getPickedAnimes().add(this);
		}
	}
}
