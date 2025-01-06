package com.example.aniwhere.controller.user;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import com.example.aniwhere.domain.user.dto.UserDTO;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.ErrorResponse;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.episodesReview.EpisodesReviewRepository;
import com.example.aniwhere.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "사용자 관련 API")
@RequiredArgsConstructor
public class UserApiController {

	private final EpisodesReviewRepository episodesReviewRepository;
	private final UserService userService;

	@Operation(
			summary = "내가 작성한 에피소드 리뷰 목록",
			description = "자신이 작성한 에피소드 리뷰 목록을 조회합니다."
	)
	@GetMapping("/users/me/episode-reviews")
	public ResponseEntity<Page<EpisodeReviewResponse>> getUserEpisodeReviews(@LoginUser Long userId, Pageable pageable) {
		Page<EpisodeReviewResponse> reviews = episodesReviewRepository.getMyEpisodeReviews(userId, pageable);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(reviews);
	}

	@Operation(
			summary = "특정 사용자가 작성한 에피소드 리뷰 목록",
			description = "특정 사용자가 작성한 에피소드 리뷰 목록을 조회합니다."
	)
	@GetMapping("/users/{nickName}/episode-reviews")
	public ResponseEntity<Page<EpisodeReviewResponse>> getUserEpisodeReviews(@PathVariable(name = "nickName") String nickName, Pageable pageable) {
		Page<EpisodeReviewResponse> reviews = episodesReviewRepository.getUserEpisodeReviews(nickName, pageable);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(reviews);
	}

	@Operation(
			summary = "본인의 정보 조회",
			description = "로그인한 사용자가 자신의 정보를 조회합니다."
	)
	@GetMapping("/users/me/myInfo")
	public ResponseEntity<UserDTO.UserInfoResponse> getMyInfo(@LoginUser Long userId) {
		UserDTO.UserInfoResponse userResponse = userService.getMyInfo(userId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userResponse);
	}

	@Operation(
			summary = "닉네임 중복체크",
			description = "사용자의 닉네임 중복체크를 시행합니다."
	)
	@GetMapping("/check/{nickName}")
	public ResponseEntity<Boolean> nickNameDupCheck(@PathVariable(name = "nickName") String nickName){
		boolean isAvailable = userService.isNicknameAvailable(nickName);
		return ResponseEntity.status(HttpStatus.OK).body(isAvailable);
	}

	@Operation(
			summary = "사용자 정보 업데이트",
			description = "사용자가 자신의 정보를 업데이트합니다."
	)
	@PostMapping("/users/me/update")
	public ResponseEntity<Void> updateUserInfo(@LoginUser Long userId, @RequestBody UserDTO.UserUpdateRequest updateRequest) {
		userService.updateUserInfo(userId, updateRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}