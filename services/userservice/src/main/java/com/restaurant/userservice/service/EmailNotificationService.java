package com.restaurant.userservice.service;

import com.restaurant.userservice.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to Ember & Oak, " + user.getFirstName() + "!";
        String body = buildWelcomeBody(user);
        send(user.getEmail(), subject, body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Welcome email sent to {} — {}", to, subject);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }

    private String buildWelcomeBody(User user) {
        return "<html><body style='font-family:sans-serif;color:#333;max-width:600px;margin:auto'>"
            + "<div style='background:#1a1a1a;padding:24px;text-align:center'>"
            + "<h1 style='color:#d4af6e;margin:0'>Ember &amp; Oak</h1></div>"
            + "<div style='padding:32px 24px'>"
            + "<h2 style='color:#1a1a1a'>Welcome, " + user.getFirstName() + "!</h2>"
            + "<p>Thank you for creating an account at <strong>Ember &amp; Oak</strong>. "
            + "We're thrilled to have you join our community of food lovers.</p>"
            + "<table style='width:100%;border-collapse:collapse;margin:24px 0;background:#f9f9f9;border-radius:6px'>"
            + "<tr><td style='padding:12px 16px;font-weight:bold;width:40%;border-bottom:1px solid #eee'>Name</td>"
            + "<td style='padding:12px 16px;border-bottom:1px solid #eee'>" + user.getFirstName() + " " + user.getLastName() + "</td></tr>"
            + "<tr><td style='padding:12px 16px;font-weight:bold;border-bottom:1px solid #eee'>Email</td>"
            + "<td style='padding:12px 16px;border-bottom:1px solid #eee'>" + user.getEmail() + "</td></tr>"
            + (user.getPhone() != null && !user.getPhone().isBlank()
                ? "<tr><td style='padding:12px 16px;font-weight:bold'>Phone</td>"
                  + "<td style='padding:12px 16px'>" + user.getPhone() + "</td></tr>"
                : "")
            + "</table>"
            + "<p>With your account you can:</p>"
            + "<ul style='line-height:2'>"
            + "<li>Browse our full menu and seasonal specials</li>"
            + "<li>Place dine-in, takeaway, or delivery orders</li>"
            + "<li>Make and manage table reservations</li>"
            + "<li>View your order &amp; reservation history</li>"
            + "</ul>"
            + "<div style='margin-top:32px;text-align:center'>"
            + "<a href='http://localhost:3000' "
            + "style='background:#d4af6e;color:#1a1a1a;padding:12px 32px;text-decoration:none;"
            + "border-radius:4px;font-weight:bold;display:inline-block'>Visit Ember &amp; Oak</a>"
            + "</div>"
            + "</div>"
            + "<div style='background:#f5f5f5;padding:16px;text-align:center;font-size:12px;color:#999'>"
            + "Ember &amp; Oak | 123 Oak Street, New York, NY"
            + "<br>If you didn't create this account, please ignore this email."
            + "</div>"
            + "</body></html>";
    }
}
