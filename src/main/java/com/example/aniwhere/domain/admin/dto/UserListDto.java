package com.example.aniwhere.domain.admin.dto;


import com.example.aniwhere.domain.user.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserListDto {

	private Long userId;
	private String email;
	private String nickname;
	private Role role;
	private String birthDay;
	private String birthYear;
}
