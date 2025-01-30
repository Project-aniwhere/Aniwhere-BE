package com.example.aniwhere.repository.user;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.controller.admin.UserSearchCondition;
import com.example.aniwhere.domain.admin.dto.UserListDto;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCustomRepository {

	PageResponse<UserListDto> getUserList(final PageRequest pageRequest);
	PageResponse<UserListDto> searchUserListByKeyword(final PageRequest pageRequest, final UserSearchCondition condition);
}
