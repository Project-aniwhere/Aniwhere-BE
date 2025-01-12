package com.example.aniwhere.repository.pickedAnime;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.pickedAnime.dto.PickedAnimeDto;
import com.example.aniwhere.domain.pickedAnime.dto.QPickedAnimeDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.domain.pickedAnime.QPickedAnime.pickedAnime;

@Repository
@RequiredArgsConstructor
public class PickedAnimeRepositoryImpl implements PickedAnimeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public PageResponse<PickedAnimeDto> getPickedAnime(Long userId, PageRequest request) {
		org.springframework.data.domain.PageRequest pageRequest = request.of();

		List<PickedAnimeDto> pickedAnimeDtoList = queryFactory
				.select(new QPickedAnimeDto(
						pickedAnime.anime.animeId,
						pickedAnime.user.id,
						pickedAnime.anime.title,
						pickedAnime.anime.poster))
				.from(pickedAnime)
				.where(pickedAnime.user.id.eq(userId))
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(pickedAnime.anime.createdAt.asc())
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(pickedAnime.count())
				.from(pickedAnime)
				.where(pickedAnime.user.id.eq(userId));

		Page<PickedAnimeDto> page = PageableExecutionUtils.getPage(
				pickedAnimeDtoList,
				pageRequest,
				countQuery::fetchOne
		);

		return new PageResponse<>(page);
	}
}
