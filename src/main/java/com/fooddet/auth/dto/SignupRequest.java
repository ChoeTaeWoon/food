//package com.example.foodapp.auth.dto;
package com.fooddet.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min=8, max=64)
    private String password;

    @Size(max=80)
    private String displayName;

    @Size(max=10)
    private String locale; // "ko" | "en"
}
