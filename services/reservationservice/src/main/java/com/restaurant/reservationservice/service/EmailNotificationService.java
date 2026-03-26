package com.restaurant.reservationservice.service;

import com.restaurant.reservationservice.model.Reservation;
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

    public void sendReservationConfirmation(Reservation res) {
        String subject = "Reservation Confirmed – Ember & Oak #" + res.getId();
        String body = buildConfirmationBody(res);
        send(res.getCustomerEmail(), subject, body);
    }

    public void sendReservationCancellation(Reservation res) {
        String subject = "Reservation Cancelled – Ember & Oak #" + res.getId();
        String body = buildCancellationBody(res);
        send(res.getCustomerEmail(), subject, body);
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
            log.info("Email sent to {} — {}", to, subject);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildConfirmationBody(Reservation res) {
        String specialRequests = (res.getSpecialRequests() != null && !res.getSpecialRequests().isBlank())
            ? "<p><strong>Special requests:</strong> " + res.getSpecialRequests() + "</p>"
            : "";

        return "<html><body style='font-family:sans-serif;color:#333;max-width:600px;margin:auto'>"
            + "<div style='background:#1a1a1a;padding:24px;text-align:center'>"
            + "<h1 style='color:#d4af6e;margin:0'>Ember &amp; Oak</h1></div>"
            + "<div style='padding:24px'>"
            + "<h2>Reservation Confirmed!</h2>"
            + "<p>Hi " + res.getCustomerName() + ", your table is booked. We look forward to seeing you!</p>"
            + "<table style='width:100%;border-collapse:collapse;margin-top:16px;background:#f9f9f9'>"
            + "<tr><td style='padding:10px;font-weight:bold;width:40%'>Reservation #</td><td style='padding:10px'>" + res.getId() + "</td></tr>"
            + "<tr><td style='padding:10px;font-weight:bold'>Date</td><td style='padding:10px'>" + res.getReservationDate() + "</td></tr>"
            + "<tr><td style='padding:10px;font-weight:bold'>Time</td><td style='padding:10px'>" + res.getReservationTime() + "</td></tr>"
            + "<tr><td style='padding:10px;font-weight:bold'>Party size</td><td style='padding:10px'>" + res.getPartySize() + " guest(s)</td></tr>"
            + "</table>"
            + specialRequests
            + "<p style='margin-top:24px'>If you need to cancel or make changes, please contact us at least 2 hours in advance.</p>"
            + "</div>"
            + "<div style='background:#f5f5f5;padding:16px;text-align:center;font-size:12px;color:#999'>"
            + "Ember &amp; Oak | 123 Oak Street, New York, NY</div>"
            + "</body></html>";
    }

    private String buildCancellationBody(Reservation res) {
        return "<html><body style='font-family:sans-serif;color:#333;max-width:600px;margin:auto'>"
            + "<div style='background:#1a1a1a;padding:24px;text-align:center'>"
            + "<h1 style='color:#d4af6e;margin:0'>Ember &amp; Oak</h1></div>"
            + "<div style='padding:24px'>"
            + "<h2>Reservation Cancelled</h2>"
            + "<p>Hi " + res.getCustomerName() + ", your reservation <strong>#" + res.getId() + "</strong> on "
            + res.getReservationDate() + " at " + res.getReservationTime() + " has been cancelled.</p>"
            + "<p>We hope to welcome you another time. You can book a new table anytime on our website.</p>"
            + "</div>"
            + "<div style='background:#f5f5f5;padding:16px;text-align:center;font-size:12px;color:#999'>"
            + "Ember &amp; Oak | 123 Oak Street, New York, NY</div>"
            + "</body></html>";
    }
}
