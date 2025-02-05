package com.example.movie15.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoleRequestDto {

    @NotBlank(message = "역할을 입력해주세요.")
    private String role;
}
