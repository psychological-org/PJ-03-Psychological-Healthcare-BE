package com.microservices.notification.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors) {

}
