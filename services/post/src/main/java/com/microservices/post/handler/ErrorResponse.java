package com.microservices.post.handler;

import java.util.Map;

public record ErrorResponse(
                Map<String, String> errors) {

}