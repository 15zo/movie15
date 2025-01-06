package com.example.movie15.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtAuthResponse {

    private String tokenAuthScheme;
    private String accessToken;

    public JwtAuthResponse(String tokenAuthScheme, String accessToken) {
        this.tokenAuthScheme = tokenAuthScheme;
        this.accessToken = accessToken;
    }
}