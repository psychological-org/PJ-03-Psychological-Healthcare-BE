package com.microservices.media.media;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private boolean success;
    private String urlOrMessage;
}
