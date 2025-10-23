package com.example.oops.cofig.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;


@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }


    @Bean
    public S3AsyncClient s3AsyncClient(S3Client s3Client) {
        // S3AsyncClient.builder()의 toBuilder()를 사용하여 S3Client가 가진
        // 인증 정보와 리전 정보를 가져와 비동기 클라이언트를 생성합니다.
        return S3AsyncClient.builder()
                .region(s3Client.serviceClientConfiguration().region())
                .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
                // .syncClient(s3Client) // 이 방식은 충돌 가능성이 있어 수동 추출로 재시도합니다.
                .build();
    }
    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        // S3Client를 사용하여 S3TransferManager를 생성합니다.
        // TransferManager와 마찬가지로 대용량 파일의 멀티파트 업로드를 처리합니다.
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                // 필요에 따라 쓰레드 풀 크기 등을 설정할 수 있습니다.
                .build();
    }
}