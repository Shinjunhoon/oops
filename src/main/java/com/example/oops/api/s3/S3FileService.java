package com.example.oops.api.s3;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService {

    private final S3TransferManager transferManager;
    private final S3Client s3Client;

    @Value("${app.s3.default-bucket}")
    private String bucketName;

    @Value("${app.s3.cloudfront-domain}")
    private String cloudfrontDomain; // CloudFront 도메인 주입

    public String uploadVideoWithTransferManager(MultipartFile file) throws IOException {


        String originalFilename = file.getOriginalFilename();
        String key = "video/" + UUID.randomUUID().toString() + "_" + originalFilename;

        // 임시 파일을 생성하여 TransferManager에 전달하는 것이 가장 안정적입니다.
        File tempFile = File.createTempFile("upload-", originalFilename);
        file.transferTo(tempFile); // MultipartFile 내용을 임시 파일에 복사

        try {
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucketName).key(key).contentType(file.getContentType()))
                    .source(tempFile.toPath()) // .sourceFile() 대신 .source() 메서드 사용
                    .build();

            // 업로드 실행
            FileUpload upload = transferManager.uploadFile(uploadFileRequest);

            // 업로드 완료를 기다립니다. (비동기 처리도 가능하지만 여기선 동기로 처리)
            upload.completionFuture().join();

            // 최종 파일 접근 URL 반환

            return "https://" + cloudfrontDomain + "/" + key;


        } finally {
            // 임시 파일 삭제
            tempFile.delete();
        }
    }
}