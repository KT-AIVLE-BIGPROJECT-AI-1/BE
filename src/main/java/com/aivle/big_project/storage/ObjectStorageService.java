package com.aivle.big_project.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    // 저장된 오브젝트의 접근 URL 반환
    String upload(MultipartFile file, String keyPrefix);
    byte[] download(String uriOrKey);
    void delete(String uriOrKey);
}
