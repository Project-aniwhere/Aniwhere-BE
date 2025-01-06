package com.example.aniwhere.domain.user;

import com.example.aniwhere.domain.pickedAnime.PickedAnime;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "USERS")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends Common {

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

	@OneToMany(mappedBy = "user")
	private List<PickedAnime> pickedAnimes = new ArrayList<>();

	// 정보 업데이트
	public User updateUser(User user) {
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.providerId = user.getProviderId();
		this.provider = user.getProvider();
		return this;
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
