package com.microservices.comment.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentNotFoundException extends RuntimeException {

    private final String msg;
}