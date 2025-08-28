package com.psms.pawn_shop_management_system.common.util;

import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class SystemUtils {

    private final ServerUtils serverUtils;
    private final JavaMailSender mailSender;

    public String removeSpace(String value) {
        if (value == null || value.isEmpty()) {
            return value; // return as is if null or empty
        }
        // Replace one or more spaces with a single underscore
        return value.trim().replaceAll("\\s+", "_");
    }

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // ensures 6 digits
        return String.valueOf(otp);
    }

    public String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void sendOtpEmail(String email, String otpCode , String mailTemplate) throws IOException, MessagingException {
        String userName = email.split("@")[0];

        String htmlTemplate = loadTemplate("templates/"+ mailTemplate +".html");
        String htmlContent = htmlTemplate
                .replace("{{username}}", userName)
                .replace("{{code}}", otpCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("🔒 Verify Code ");
        helper.setText(htmlContent, true);
        helper.addInline("logoImage", new ClassPathResource("templates/logo/logo.png"));
        mailSender.send(message);
    }
}
