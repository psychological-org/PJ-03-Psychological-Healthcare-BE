package com.microservices.media.media;

import com.microservices.media.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public MediaService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + filename;

        } catch (S3Exception s3e) {
            log.error("S3 error: {}", s3e.awsErrorDetails().errorMessage());
            throw new FileUploadException("S3 upload failed: " + s3e.awsErrorDetails().errorMessage(), s3e);
        } catch (IOException io) {
            log.error("IO error: {}", io.getMessage());
            throw io;
        }
    }
}
