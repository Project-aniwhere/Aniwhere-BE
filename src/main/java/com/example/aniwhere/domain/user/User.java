package com.example.aniwhere.domain.user;

import com.example.aniwhere.domain.animeReview.AnimeReview;
import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "birthyear")
	private String birthyear;

	@Column(name = "birthday")
	private String birthday;

	@Column(name = "sex")
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Column(name = "provider")
	private String provider;

	@Column(name = "providerId")
	private String providerId;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PickedAnime> pickedAnimes = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EpisodeReviews> episodeReviews = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AnimeReview> animeReviews = new ArrayList<>();


	// 정보 업데이트
	public User updateUser(User user) {
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.providerId = user.getProviderId();
		this.provider = user.getProvider();
		return this;
	}

	@Builder
	public User(String nickname, String email, String password, Role role, String birthyear, String birthday, Sex sex, String provider, String providerId) {
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.role = role;
		this.birthyear = birthyear;
		this.birthday = birthday;
		this.sex = sex;
		this.provider = provider;
		this.providerId = providerId;
	}

	public User updateUserInfo(User updatedUser) {
		if (!this.nickname.equals(updatedUser.getNickname())) {
			this.nickname = updatedUser.getNickname();
		}
		if (!this.email.equals(updatedUser.getEmail())) {
			this.email = updatedUser.getEmail();
		}
		if (!this.password.equals(updatedUser.getPassword())) {
			this.password = updatedUser.getPassword();
		}
		return this;
	}
}
