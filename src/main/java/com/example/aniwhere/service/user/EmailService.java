package com.example.aniwhere.service.user;

import com.example.aniwhere.global.error.exception.MailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private static final String EMAIL_VERIFICATION_TITLE = "Aniwhere 이메일 2차 인증 코드 메일입니다.";
	private final JavaMailSender emailSender;

	/**
	 * 인증 코드를 포함한 이메일 발송
	 * @param toEmail
	 * @param title
	 * @param text
	 * @return void
	 */
	public void sendEmail(String toEmail, String title, String text) {

		SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
		try {
			emailSender.send(emailForm);
		} catch (RuntimeException e) {
			log.error("메일 발송에 실패했습니다. 발송 이메일:{}, 제목:{}, 내용:{}", toEmail, title, text);
			throw new MailSendException(SERVICE_UNAVAILABLE, false);
		}
 	}

	private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(toEmail);
		simpleMailMessage.setSubject(EMAIL_VERIFICATION_TITLE);
		simpleMailMessage.setText("인증 코드는 " + text + "입니다.");
		return simpleMailMessage;
	}
}
