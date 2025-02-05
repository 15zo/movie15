package com.example.movie15.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CheckPasswordRequestDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
