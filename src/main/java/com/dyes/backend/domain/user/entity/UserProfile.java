package com.dyes.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Data
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

}
