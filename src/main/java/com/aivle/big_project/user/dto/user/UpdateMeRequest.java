package com.aivle.big_project.user.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class UpdateMeRequest {
    private MultipartFile profileImg; // form-data File
    private String phoneNumber;

    private String jurisdictionStation;
    private String department;
    private String rankName;
}
