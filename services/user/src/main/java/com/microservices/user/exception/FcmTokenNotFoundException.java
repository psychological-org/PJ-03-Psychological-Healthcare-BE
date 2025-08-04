package com.microservices.user.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcmTokenNotFoundException extends RuntimeException {
    private final String msg;
}