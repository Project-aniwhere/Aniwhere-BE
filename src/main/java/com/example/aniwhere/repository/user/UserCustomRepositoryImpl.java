package com.example.aniwhere.repository.user;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.controller.admin.UserSearchCondition;
import com.example.aniwhere.domain.admin.dto.UserListDto;
import com.example.aniwhere.domain.user.Sex;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public PageResponse<UserListDto> getUserList(PageRequest request) {
		org.springframework.data.domain.PageRequest pageRequest = request.of();

		List<UserListDto> result = queryFactory
				.select(Projections.constructor(UserListDto.class,
						user.id.as("userId"),
						user.email,
						user.nickname,
						user.role,
						user.birthday.as("birthDay"),
						user.birthyear.as("birthYear")
				))
				.from(user)
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(user.createdAt.asc())
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(user.count())
				.from(user);

		Page<UserListDto> page = PageableExecutionUtils.getPage(
				result,
				pageRequest,
				countQuery::fetchOne
		);

		return new PageResponse<>(page);
	}

	@Override
	public PageResponse<UserListDto> searchUserListByKeyword(PageRequest request, UserSearchCondition condition) {
		org.springframework.data.domain.PageRequest pageRequest = request.of();

		List<UserListDto> result = queryFactory
				.select(Projections.constructor(UserListDto.class,
						user.id.as("userId"),
						user.email,
						user.nickname,
						user.role,
						user.birthday.as("birthDay"),
						user.birthyear.as("birthYear")
				))
				.from(user)
				.where(emailEq(condition.getEmail()),
						usernameEq(condition.getNickname()),
						sexEq(condition.getSex()))
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(user.createdAt.asc())
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(user.count())
				.from(user)
				.where(emailEq(condition.getEmail()),
					usernameEq(condition.getNickname()),
					sexEq(condition.getSex()));

		Page<UserListDto> page = PageableExecutionUtils.getPage(
				result,
				pageRequest,
				countQuery::fetchOne
		);

		return new PageResponse<>(page);
	}

	private Predicate emailEq(String emailCond) {
		if (emailCond != null) {
			return user.email.eq(emailCond);
		}
		return null;
	}

	private Predicate usernameEq(String nicknameCond) {
		if (nicknameCond != null) {
			return user.nickname.eq(nicknameCond);
		}
		return null;
	}

	private Predicate sexEq(Sex sexCond) {
		if (sexCond != null) {
			return user.sex.eq(sexCond);
		}
		return null;
	}
}
