package com.microservices.notification.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationNotFoundException extends RuntimeException {

    private final String msg;
}