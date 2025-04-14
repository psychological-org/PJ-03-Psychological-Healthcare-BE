package com.microservices.media.media;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Media controller";
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = mediaService.uploadFile(file);
        return ResponseEntity.ok(new UploadResponse(true, fileUrl));
    }
}
