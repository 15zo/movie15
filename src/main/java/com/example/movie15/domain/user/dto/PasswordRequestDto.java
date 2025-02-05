package com.example.movie15.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordRequestDto {

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    public PasswordRequestDto(String password) {
        this.password = password;
    }
}
