package com.dyes.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private String id;

    private String nickName;

    private String email;

    private String profileImg;

    private String contactNumber;
    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateUserProfile(String nickName, String email, String profileImg, String contactNumber, Address address) {
        this.nickName = nickName;
        this.email = email;
        this.profileImg = profileImg;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }
}
