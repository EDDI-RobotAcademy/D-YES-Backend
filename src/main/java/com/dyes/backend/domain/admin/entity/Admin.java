package com.dyes.backend.domain.admin.entity;

import com.dyes.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString(exclude = "user")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Builder
    public Admin(String name, User user, RoleType roleType) {
        this.name = name;
        this.user = user;
        this.roleType = roleType;
    }
}
