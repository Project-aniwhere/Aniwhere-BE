package com.example.aniwhere.controller.admin;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.admin.dto.EvaluationRequestDto;
import com.example.aniwhere.domain.admin.dto.EvaluationResponseDto;
import com.example.aniwhere.domain.admin.dto.UserListDto;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자용 API")
@RequestMapping("/api")
public class AdminApiController {

	private final AdminService adminService;
	private final UserRepository userRepository;

	@Operation(
			summary = "[관리자용] 작품 요청에 대한 평가",
			description = "승인 / 반려와 함께 그에 대한 코멘트를 남길 수 있다."
	)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/admin/evaluation")
	public ResponseEntity<EvaluationResponseDto> evaluate(final @RequestBody EvaluationRequestDto dto) {

		EvaluationResponseDto result = adminService.processEvaluation(dto);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(result);
	}

	@Operation(
			summary = "[관리자용] 전체 유저 조회",
			description = "관리자가 전체 유저를 조회할 수 있다."
	)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/manage")
	public ResponseEntity<PageResponse<UserListDto>> getAllUsers(final PageRequest pageRequest) {

		PageResponse<UserListDto> users = userRepository.getUserList(pageRequest);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(users);
	}

	@Operation(
			summary = "[관리자용] 필터링을 통한 유저 조회",
			description = "성별 / 닉네임 / 이메일을 통해 유저를 조회할 수 있다."
	)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/filter")
	public ResponseEntity<PageResponse<UserListDto>> getUsersByKeyword(final PageRequest pageRequest,
																	   @RequestParam(value = "email", required = false) String email,
																	   @RequestParam(value = "nickname", required = false) String nickname,
																	   @RequestParam(value = "sex", required = false) Sex sex) {

		UserSearchCondition userSearchCondition = new UserSearchCondition(nickname, email, sex);
		PageResponse<UserListDto> result = userRepository.searchUserListByKeyword(pageRequest, userSearchCondition);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(result);
	}

}
