package com.example.aniwhere.service.notification.event;

import com.example.aniwhere.domain.admin.dto.Status;
import com.example.aniwhere.domain.user.User;

public record AdminResponseEvent(Long historyId, User reply, Status status, String content) {

}
