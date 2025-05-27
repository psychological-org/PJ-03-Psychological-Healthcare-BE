package com.microservices.post.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostNotFoundException extends RuntimeException {

    private final String msg;
}