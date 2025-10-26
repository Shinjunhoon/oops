package com.example.oops.api.email.service;

import com.example.oops.api.email.RedisUtil;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long expirationMillis;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String createCode(){
        Random random = new Random();
        // 900000 = 999999 (최대) - 100000 (최소) + 1
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public String sendEmail(String email) throws MessagingException {
        if(redisUtil.existData(email)){
            redisUtil.deleteData(email);
        }

        String authCode = createCode();


        MimeMessage message = mailSender.createMimeMessage();
        long expirationTime = TimeUnit.MILLISECONDS.toMillis(expirationMillis);

        Duration duration = Duration.ofMillis(expirationMillis);
        redisUtil.setData(email,authCode,duration);

        Context context = new Context();
        context.setVariable("authCode", authCode);
        context.setVariable("expiryMinutes", expirationTime);

        String emailContext = templateEngine.process("email/auth-code-template", context);

        try{
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true: HTML 본문 지원
        helper.setTo(email);
        helper.setSubject("[OOPS] 이메일 본인 인증 코드입니다.");
        helper.setFrom(senderEmail);
        helper.setText(emailContext, true);
        mailSender.send(message);
        }catch (MessagingException e){
            throw new OopsException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        return authCode;
    }


    public Boolean verifyCode(String email,String authCode) throws MessagingException {

        String storedCode = redisUtil.getData(email);

        if(storedCode == null){
            return false;
        }

        boolean isVerified = storedCode.equals(authCode);

        if(isVerified){
            redisUtil.deleteData(email);
        }
        else{
            log.warn("이메일 인증 실패");
        }
        return isVerified;
    }
}
