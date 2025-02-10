package com.example.aniwhere.service.notification.event;

import com.example.aniwhere.domain.user.User;

public record UserRequestEvent(User sender, User receiver, String content) {

}
