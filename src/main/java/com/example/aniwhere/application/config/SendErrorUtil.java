package com.example.aniwhere.application.config;

import com.example.aniwhere.global.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import static com.example.aniwhere.global.error.ErrorCode.*;

import java.io.IOException;

public class SendErrorUtil {

	public static void sendUnauthorizedErrorResponse(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
		String errorResponse = objectMapper.writeValueAsString(ErrorResponse.of(UNAUTHORIZED));
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		writeErrorResponse(response, errorResponse);
	}

	private static void writeErrorResponse(HttpServletResponse response, String errorResponse) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(errorResponse);
	}
}
