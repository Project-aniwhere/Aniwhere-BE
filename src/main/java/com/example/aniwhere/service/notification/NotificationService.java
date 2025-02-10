package com.example.aniwhere.service.notification;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.notification.Notification;
import com.example.aniwhere.domain.notification.NotificationType;
import com.example.aniwhere.domain.notification.dto.NotificationDto;
import com.example.aniwhere.domain.notification.dto.NotificationsResponseDto;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.HistoryException;
import com.example.aniwhere.global.error.exception.NotificationException;
import com.example.aniwhere.repository.history.HistoryRepository;
import com.example.aniwhere.repository.notification.NotificationRepository;
import com.example.aniwhere.service.notification.event.AdminResponseEvent;
import com.example.aniwhere.service.notification.event.UserRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.aniwhere.domain.history.ReplyStatus.COMPLETED;
import static com.example.aniwhere.domain.history.ReplyStatus.PENDING;
import static com.example.aniwhere.domain.notification.NotificationType.RESPONSE;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j @RequiredArgsConstructor
@Service @Transactional
public class NotificationService {

	private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	private final NotificationRepository notificationRepository;

	// 단일 서버의 경우 SSE가 단일 서버에서만 저장을 하기 때문에 다른 서버 측은 해당 연결 정보를 가지고 있지 않다.
	// 레디스를 두어 공유하도록 설계
	private final RedisOperations<String, NotificationDto> eventRedisOperations;	// SSE & Redis - Scale out
	private final RedisMessageListenerContainer redisMessageListenerContainer;		// SSE & Redis - Scale out
	private final ObjectMapper objectMapper;
	private final HistoryRepository historyRepository;

	// 사용자가 관리자에게 작품 요청을 보낼 때 알림 발송(관리자에게)
	// 알림 전송과 비즈니스 로직의 트랜잭션을 분리 - 트랜잭션 생성에 따른 데드락 발생 여부도 추후 고려
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	//@Async("threadPoolTaskExecutor")
	@TransactionalEventListener
	public void send(UserRequestEvent userRequestEvent) {

		User receiver = userRequestEvent.receiver();
		User sender = userRequestEvent.sender();
		String content = userRequestEvent.content();

		String message = String.format("사용자 [%s]님이 관리자에게 요청을 보냈습니다.", sender.getNickname());

		// 알림 생성
		Notification notification = Notification.builder()
				.user(receiver)
				.message(message)
				.notificationType(NotificationType.REQUEST)
				.build();
		Notification savedNotification = notificationRepository.save(notification);

		// 히스토리 내역 생성(사용자 요청시 최초 생성)
		History history = History.builder()
				.sender(sender)
				.receiver(receiver)
				.status(PENDING)
				.content(content)
				.build();
		historyRepository.save(history);

		NotificationDto notificationDto = NotificationDto.fromEntity(savedNotification);

		String channelName = getChannelName(String.valueOf(receiver.getId()));
		eventRedisOperations.convertAndSend(channelName, notificationDto);
	}

	// 관리자가 받은 요청에 대해서 처리한 후 요청자에게 응답을 보낸다.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	//@Async("threadPoolTaskExecutor")
	@TransactionalEventListener
	public void send(AdminResponseEvent adminResponseEvent) {

		LocalDateTime now = LocalDateTime.now();

		User reply = adminResponseEvent.reply();
		String content = adminResponseEvent.content();

		// 히스토리 업데이트
		History history = historyRepository.findById(adminResponseEvent.historyId())
				.orElseThrow(() -> new HistoryException(NOT_FOUND_HISTORY));
		history.historyUpdate(COMPLETED, now);

		// 관리자가 사용자에게 보내는 알림
		Notification notification = Notification.builder()
				.user(reply)
				.message(content)
				.notificationType(RESPONSE)
				.build();
		Notification savedNotification = notificationRepository.save(notification);
		NotificationDto notificationDto = NotificationDto.fromEntity(savedNotification);

		String channelName = getChannelName(String.valueOf(reply.getId()));

		// 레디스 채널에 메시지 publish
		eventRedisOperations.convertAndSend(channelName, notificationDto);
	}

	@Transactional
	public SseEmitter subscribe(Long userId) throws IOException {
		log.info("정상적으로 전송되는지 체크");
		String id = String.valueOf(userId);
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

		// 503 오류 방지
		sseEmitter.send(SseEmitter.event()
				.id(id)
				.name("sse"));
		emitters.add(sseEmitter);

		MessageListener messageListener = (message, pattern) -> {
			NotificationDto notificationDto = serialize(message);
			sendToClient(sseEmitter, id, notificationDto);
		};

		this.redisMessageListenerContainer.addMessageListener(messageListener, ChannelTopic.of(getChannelName(id)));
		checkEmitterStatus(sseEmitter, messageListener);
		return sseEmitter;
	}

	private NotificationDto serialize(Message message) {
		try {
			Notification notification = this.objectMapper.readValue(message.getBody(), Notification.class);
			return NotificationDto.fromEntity(notification);
		} catch (IOException e) {
			throw new NotificationException(INVALID_REDIS_MESSAGE);
		}
	}

	private void sendToClient(final SseEmitter emitter, final String id, final Object data) {
		try {
			log.info("전송중!");
			emitter.send(SseEmitter.event()
					.id(id)
					.name("sse")
					.data(data));
			log.info("전송완료");
		} catch (IOException e) {
			emitters.remove(emitter);
			log.error("SSE 연결이 올바르지 않습니다. 해당 memberID={}", id);
		}
	}

	private void checkEmitterStatus(SseEmitter sseEmitter, MessageListener messageListener) {
		sseEmitter.onCompletion(() -> {
			emitters.remove(sseEmitter);
			this.redisMessageListenerContainer.removeMessageListener(messageListener);
		});
		sseEmitter.onTimeout(() -> {
			emitters.remove(sseEmitter);
			this.redisMessageListenerContainer.removeMessageListener(messageListener);
		});
	}

	private String getChannelName(final String userId) {
		return "aniwhere:topics:" + userId;
	}

	public NotificationsResponseDto findAllByUserIdAndIsRead(Long userId) {
		List<NotificationDto> response = notificationRepository.findAllByUserIdAndIsReadFalse(userId)
				.stream()
				.map(NotificationDto::fromEntity)
				.toList();

		long unreadCount = response.size();

		return NotificationsResponseDto.of(response, unreadCount);
	}

	@Transactional
	public void readNotification(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));
		notification.read();
	}

	@Transactional
	public void readAllNotifications(Long userId) {
		notificationRepository.updateNotificationRead(userId);
	}
}
