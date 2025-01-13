package com.example.movie15.domain.user.dto;

import com.example.movie15.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRequestDto {

    @Email(message = "이메일 형식이 아닙니다.")
    @Size(max = 320)
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "name은 필수 입력 항목입니다.")
    @Size(max = 10)
    private String name;

    public UserRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.name = nickname;
    }

    public User toEntity() {
        return new User(
                this.email,
                this.password,
                this.name
        );
    }

    public void updatePassword(String encryptedPassword) {

        this.password = encryptedPassword;
    }
}