package com.example.aniwhere.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.STRING)
@Getter
public enum Sex {
	male("남자"),
	female("여자");

	private final String description;

	Sex(String description) {
		this.description = description;
	}
}
