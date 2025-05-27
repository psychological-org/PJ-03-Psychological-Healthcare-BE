package com.microservices.topic.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TopicNotFoundException extends RuntimeException {

    private final String msg;
}