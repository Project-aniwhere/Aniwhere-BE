package com.example.aniwhere.controller.notification;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.notification.dto.NotificationsResponseDto;
import com.example.aniwhere.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationApiController {

	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(@LoginUser Long userId) throws IOException {
		return ResponseEntity.ok(notificationService.subscribe(userId));
	}

	@GetMapping("/notifications")
	public ResponseEntity<NotificationsResponseDto> notifications(@LoginUser Long userId) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(notificationService.findAllByUserIdAndIsRead(userId));
	}

	@PutMapping("/notifications/{id}")
	public ResponseEntity<Void> readNotification(@PathVariable(name = "id") Long id) {
		notificationService.readNotification(id);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@PutMapping("/notifications/all")
	public ResponseEntity<Void> readAllNotifications(@LoginUser Long userId) {
		notificationService.readAllNotifications(userId);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}
}
