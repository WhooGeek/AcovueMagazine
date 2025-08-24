package com.AcovueMagazine.User.Aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "acovue_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    private String pwd;
    private String name;
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.USER;

    public void encryptPassword(String encode) {
        this.pwd = encode;
    }
}
