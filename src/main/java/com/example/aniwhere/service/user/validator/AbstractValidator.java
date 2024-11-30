package com.example.aniwhere.service.user.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
public abstract class AbstractValidator<T> implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void validate(Object target, Errors errors) {
		try {
			doValidate((T) target, errors);
		} catch (RuntimeException e) {
			log.error("중복 오류 발생");
			throw e;
		}
	}

	protected abstract void doValidate(final T dto, final Errors errors);
}
