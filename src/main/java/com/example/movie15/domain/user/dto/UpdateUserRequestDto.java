package com.example.movie15.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 비밀번호와 닉네임은 선택적으로 수정 가능
@Getter
public class UpdateUserRequestDto {

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    private String oldPassword;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;

    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;
    }
