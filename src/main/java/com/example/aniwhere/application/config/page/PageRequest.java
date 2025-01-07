package com.example.aniwhere.application.config.page;

import lombok.Getter;
import lombok.Setter;

import static org.springframework.data.domain.Sort.*;

@Getter @Setter
public class PageRequest {

	private int page;
	private int size;
	private Direction direction;

	public void setPage(int page) {
		this.page = page <= 0 ? 1 : page;
	}

	public void setSize(int size) {
		int DEFAULT_SIZE = 20;
		int MAX_SIZE = 40;
		this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public org.springframework.data.domain.PageRequest of() {
		return org.springframework.data.domain.PageRequest.of(page - 1, size, direction, "created_at");
	}
}