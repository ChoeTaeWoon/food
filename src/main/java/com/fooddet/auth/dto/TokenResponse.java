//package com.example.foodapp.auth.dto;
package com.fooddet.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String tokenType; // "Bearer"
    private long expiresIn;   // seconds
}
