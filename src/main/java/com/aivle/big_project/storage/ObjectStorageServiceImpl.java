package com.aivle.big_project.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service @Slf4j
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService{

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${spring.storage.s3.bucket}")
    private String bucket;

    @Value("${spring.storage.s3.presign.put-expire-minutes:10}")
    private long putExpireMinutes;

    @Value("${spring.storage.s3.presign.get-expire-minutes:10}")
    private long getExpireMinutes;

    // (1) 백엔드가 MultipartFile 받아서 S3에 업로드
    public String upload(MultipartFile file, String keyPrefix) {
        try {
            String original = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                    ? "file"
                    : file.getOriginalFilename();

            String safeName = URLEncoder.encode(original, StandardCharsets.UTF_8);

            //  keyPrefix가 '/'로 끝나면 prefix로 간주하고 원본 파일명을 뒤에 붙이고,
            //  아니면 완성 key로 간주해서 그대로 사용
            String key = keyPrefix.endsWith("/") ? (keyPrefix + safeName) : keyPrefix;

            // content-type : 비어있으면 'application/octet-stream 처리
            String contentType = (file.getContentType() == null || file.getContentType().isBlank())
                    ? "application/octet-stream"
                    : file.getContentType();


            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            var putRes = s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
            log.info("[S3-UPLOAD][PUT_OK] eTag={}, versionId={}", putRes.eTag(), putRes.versionId());

            var head = s3.headObject(b -> b.bucket(bucket).key(key));
            log.info("[S3-UPLOAD][HEAD_OK] contentLength={}, lastModified={}", head.contentLength(), head.lastModified());
            // DB에는 key만 저장
            return key;
        } catch (Exception e) {
            log.error("[S3-UPLOAD][FAIL] bucket={}, keyPrefix={}, originalName={}", bucket, keyPrefix, file.getOriginalFilename(), e);
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    // (2-a) 클라이언트 조회 + AI 파일 전송: presigned GET 발급
    public String presignGetUrl(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(getExpireMinutes))
                .getObjectRequest(getReq)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
        return presigned.url().toString();
    }

    // (2-b) 클라이언트 다운로드용: Content-Disposition attachment 지정
    public String presignDownloadUrl(String key, String downloadFileName) {
        String safe = (downloadFileName == null || downloadFileName.isBlank()) ? "file" : downloadFileName;
        String encoded = URLEncoder.encode(safe, StandardCharsets.UTF_8);

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename*=UTF-8''" + encoded)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(getExpireMinutes))
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(presignReq).url().toString();
    }

    // (옵션) 백엔드가 직접 다운로드해서 ResponseEntity로 내려줄 때 사용
    public byte[] download(String s3UriOrKey) {
        String key = toKey(s3UriOrKey);

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3.getObjectAsBytes(req).asByteArray();
    }

    public void delete(String s3UriOrKey) {
        String key = toKey(s3UriOrKey);

        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3.deleteObject(req);
    }

    private String toKey(String s3UriOrKey) {
        if (s3UriOrKey.startsWith("s3://")) {
            if (s3UriOrKey == null) return null;
            // s3://bucket/key...
            String withoutScheme = s3UriOrKey.substring("s3://".length());
            int slashIdx = withoutScheme.indexOf('/');
            if (slashIdx < 0 || slashIdx == withoutScheme.length() - 1) {
                throw new IllegalArgumentException("Invalid S3 URI: " + s3UriOrKey);
            }
            return withoutScheme.substring(slashIdx + 1);
        }
        return s3UriOrKey;
    }
}
