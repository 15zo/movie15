package com.example.movie15.domain.user.dto;

import com.example.movie15.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
// 회원 가입 요청
@NoArgsConstructor
@Getter
public class UserRequestDto {

    @Email(message = "이메일 형식이 아닙니다.")
    @Size(max = 320)
    private String email;

    @NotBlank(message = "name은 필수 입력 항목입니다.")
    @Size(max = 10)
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "새 비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    public UserRequestDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User toEntity() {
        return new User(
                this.email,
                this.password,
                this.name
        );
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 테스트용 생성자
    public UserRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
