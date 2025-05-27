package com.microservices.follow.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FollowNotFoundException extends RuntimeException {

    private final String msg;
}