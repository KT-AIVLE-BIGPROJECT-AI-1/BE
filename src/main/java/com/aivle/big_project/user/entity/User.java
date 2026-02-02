package com.aivle.big_project.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "USERS", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false, length=50)
    private String name;

    @Column(nullable = false, length=225, unique = true)
    private String email;

    @Column(nullable = false, length=225)
    private String password;

    @Column(name = "phone_number", nullable = false, length=50)
    private String phoneNumber;

    @Column(name = "profile_img", length=225)
    private String profileImg; // nullable

    @Column(name = "jurisdiction_station", nullable = false, length=100)
    private String jurisdictionStation;

    @Column(nullable = false, length=100)
    private String department;

    @Column(name = "rank_name", nullable = false, length=50)
    private String rankName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "refresh_token_hash", length = 128)
    private String refreshTokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if(this.role == null)
            this.role = Role.INVESTIGATOR;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateMe(String profileImg,
                         String phoneNumber,
                         String jurisdictionStation,
                         String department,
                         String rankName) {
        if (profileImg != null) this.profileImg = profileImg;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (jurisdictionStation != null) this.jurisdictionStation = jurisdictionStation;
        if (department != null) this.department = department;
        if (rankName != null) this.rankName = rankName;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public void clearRefreshTokenHash() {
        this.refreshTokenHash = null;
    }
}
