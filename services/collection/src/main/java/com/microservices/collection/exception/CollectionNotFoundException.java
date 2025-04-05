package com.microservices.collection.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectionNotFoundException extends RuntimeException {

    private final String msg;
}