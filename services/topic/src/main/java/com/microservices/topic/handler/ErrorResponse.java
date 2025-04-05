package com.microservices.topic.handler;

import java.util.Map;

public record ErrorResponse(
                Map<String, String> errors) {

}