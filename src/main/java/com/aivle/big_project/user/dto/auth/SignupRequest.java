package com.aivle.big_project.user.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class SignupRequest {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    private String phoneNumber;
    private String jurisdictionStation;
    private String department;
    private String rankName;
    @JsonIgnore
    private MultipartFile profileImg;


}
