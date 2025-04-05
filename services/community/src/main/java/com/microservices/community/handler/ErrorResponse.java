package com.microservices.community.handler;

import java.util.Map;

public record ErrorResponse(
                Map<String, String> errors) {

}