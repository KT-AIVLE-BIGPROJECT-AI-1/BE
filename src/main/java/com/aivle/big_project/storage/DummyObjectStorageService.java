package com.aivle.big_project.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class DummyObjectStorageService implements ObjectStorageService {
    @Override
    public String upload(MultipartFile file, String keyPrefix) {
        String ext = extractExt(file.getOriginalFilename());
        return keyPrefix + "/" + UUID.randomUUID() + ext;
    }

    private String extractExt(String filename) {
        if(filename == null) return "";
        int idx = filename.lastIndexOf('.');
        return (idx >= 0) ? filename.substring(idx) : "";
    }
}
