package com.kitzbuhel.idpauthservice.identities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, name = "is_logged_in")
    private Boolean isLoggedIn;

    @Column(nullable = false, name = "timestamp")
    private Date timestamp;
}
