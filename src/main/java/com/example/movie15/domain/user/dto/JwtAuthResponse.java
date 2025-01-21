package com.example.movie15.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtAuthResponse {

    private String tokenAuthScheme;
    private String accessToken;
    private String refreshToken;

    public JwtAuthResponse(String tokenAuthScheme, String accessToken, String refreshToken) {
        this.tokenAuthScheme = tokenAuthScheme;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JwtAuthResponse(String tokenAuthScheme, String accessToken) {
        this.tokenAuthScheme = tokenAuthScheme;
        this.accessToken = accessToken;
    }
}