package com.microservices.community.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommunityNotFoundException extends RuntimeException {

    private final String msg;
}