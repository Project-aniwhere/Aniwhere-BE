package com.example.aniwhere.application.auth.resolver;

import com.example.aniwhere.global.error.exception.UserException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.example.aniwhere.application.auth.jwt.dto.JwtAuthentication;

import java.util.Objects;

import static com.example.aniwhere.global.error.ErrorCode.UNAUTHORIZED;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasParameterAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
		boolean hasLongParameterType = parameter.getParameterType().isAssignableFrom(Long.class);
		return hasLongParameterType & hasParameterAnnotation;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		checkAuthenticated(authentication);
		JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication.getPrincipal();
		return jwtAuthentication.userId();
	}

	private void checkAuthenticated(Authentication authentication) {
		if(Objects.isNull(authentication)) {
			throw new UserException(UNAUTHORIZED);
		}
	}
}
