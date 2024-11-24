package com.example.aniwhere.domain.user;

import com.example.aniwhere.global.error.exception.UserException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;

import static com.example.aniwhere.global.error.ErrorCode.*;

@JsonFormat(shape = JsonFormat.Shape.STRING)
@Getter
public enum Sex {
	male("남자"),
	female("여자");

	private final String description;

	Sex(String description) {
		this.description = description;
	}

	@JsonCreator
	public static Sex parsing(String input) {
		return Arrays.stream(Sex.values())
				.filter(type -> type.getDescription().equals(input) ||
						type.name().equalsIgnoreCase(input))
				.findFirst()
				.orElseThrow(() -> new UserException(INVALID_INPUT_VALUE));
	}
}
