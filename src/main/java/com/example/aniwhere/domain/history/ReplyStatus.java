package com.example.aniwhere.domain.history;

import lombok.Getter;

@Getter
public enum ReplyStatus {

	PENDING("처리 중"),  		// 관리자 검토 단계
	COMPLETED("처리 완료");	// 답변 완료

	private final String description;

	ReplyStatus(String description) {
		this.description = description;
	}

}
