package com.collusion.api.domain.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long         id;
    private String       firstName;
    private String       lastName;
    private String       email;
    private String       passwordSalt;   // never serialized to client
    private String       passwordHash;   // never serialized to client
    private List<String> roles;          // populated from user_roles join
}