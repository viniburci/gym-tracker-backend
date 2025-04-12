package com.burci.security.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMinDTO {
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
}
