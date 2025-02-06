package com.example.movie15.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupEmailSenderService {

    private final JavaMailSender mailSender;

    public String sendVerificationEmail(String userEmail) throws MessagingException {
        // UUID 를 사용해 고유한 토큰 생성
        String token = UUID.randomUUID().toString();

        // TODO : 배포 시 주소 변경 필요
        // 사용자가 클릭할 인증 링크
        String verificationLink = "15.164.0.230:8080/api/verify?token=" + token;

        // 이메일 설정 (HTML)
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        // html 꾸미기 - GPT
        String htmlMessage = """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
            <h1 style="color: #4CAF50; text-align: center;">회원가입 인증 이메일</h1>
            <p style="font-size: 16px; color: #333;">
                회원가입을 완료하려면 아래 버튼을 클릭하여 이메일 인증을 진행해주세요.
            </p>
            <div style="text-align: center; margin: 20px 0;">
                <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; font-size: 16px; border-radius: 5px;">
                    이메일 인증하기
                </a>
            </div>
            <p style="font-size: 14px; color: #999;">
                만약 위 버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저 주소창에 붙여넣기 하세요:
            </p>
            <p style="font-size: 14px; color: #555; word-break: break-all;">%s</p>
            <hr style="border: none; border-top: 1px solid #ddd;">
            <p style="font-size: 12px; color: #777; text-align: center;">
                이 메일은 자동으로 발송된 메일입니다. 문의사항이 있으시면 audgh919@gmail.com 으로 연락주세요.
            </p>
        </div>
    """.formatted(verificationLink, verificationLink);

        helper.setTo(userEmail); // 누구에게 보내는지
        helper.setSubject("회원가입 인증 이메일입니다!"); // 이메일 제목
        helper.setText(htmlMessage, true); // HTML 본문 내용 설정

        // 이메일 발송
        mailSender.send(mimeMessage);

        return token; // 생성한 토큰 반환
    }
}
