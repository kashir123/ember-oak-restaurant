package com.restaurant.orderservice.service;

import com.restaurant.orderservice.model.Order;
import com.restaurant.orderservice.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public void sendOrderConfirmation(Order order) {
        String subject = "Order Confirmed – Ember & Oak #" + order.getId();
        String body = buildConfirmationBody(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendOrderCancellation(Order order) {
        String subject = "Order Cancelled – Ember & Oak #" + order.getId();
        String body = buildCancellationBody(order);
        send(order.getCustomerEmail(), subject, body);
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

    private String buildConfirmationBody(Order order) {
        StringBuilder items = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                items.append(String.format(
                    "<tr><td>%s</td><td style='text-align:center'>%d</td><td style='text-align:right'>$%.2f</td></tr>",
                    item.getMenuItemName(), item.getQuantity(), item.getSubtotal()
                ));
            }
        }

        BigDecimal subtotal = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.0875")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        return "<html><body style='font-family:sans-serif;color:#333;max-width:600px;margin:auto'>"
            + "<div style='background:#1a1a1a;padding:24px;text-align:center'>"
            + "<h1 style='color:#d4af6e;margin:0'>Ember &amp; Oak</h1></div>"
            + "<div style='padding:24px'>"
            + "<h2>Thanks for your order, " + order.getCustomerName() + "!</h2>"
            + "<p>Your order <strong>#" + order.getId() + "</strong> has been received and is being prepared.</p>"
            + "<p><strong>Order type:</strong> " + order.getOrderType() + "</p>"
            + (order.getDeliveryAddress() != null ? "<p><strong>Delivery address:</strong> " + order.getDeliveryAddress() + "</p>" : "")
            + (order.getTableNumber() != null ? "<p><strong>Table:</strong> " + order.getTableNumber() + "</p>" : "")
            + "<table style='width:100%;border-collapse:collapse;margin-top:16px'>"
            + "<thead><tr style='background:#f5f5f5'>"
            + "<th style='text-align:left;padding:8px'>Item</th>"
            + "<th style='text-align:center;padding:8px'>Qty</th>"
            + "<th style='text-align:right;padding:8px'>Subtotal</th></tr></thead>"
            + "<tbody>" + items + "</tbody>"
            + "<tfoot>"
            + "<tr><td colspan='2' style='text-align:right;padding:6px'>Subtotal</td><td style='text-align:right;padding:6px'>$" + String.format("%.2f", subtotal) + "</td></tr>"
            + "<tr><td colspan='2' style='text-align:right;padding:6px'>Tax (8.75%)</td><td style='text-align:right;padding:6px'>$" + String.format("%.2f", tax) + "</td></tr>"
            + "<tr style='font-weight:bold'><td colspan='2' style='text-align:right;padding:8px'>Total</td><td style='text-align:right;padding:8px'>$" + String.format("%.2f", total) + "</td></tr>"
            + "</tfoot></table>"
            + "</div>"
            + "<div style='background:#f5f5f5;padding:16px;text-align:center;font-size:12px;color:#999'>"
            + "Ember &amp; Oak | 123 Oak Street, New York, NY</div>"
            + "</body></html>";
    }

    private String buildCancellationBody(Order order) {
        return "<html><body style='font-family:sans-serif;color:#333;max-width:600px;margin:auto'>"
            + "<div style='background:#1a1a1a;padding:24px;text-align:center'>"
            + "<h1 style='color:#d4af6e;margin:0'>Ember &amp; Oak</h1></div>"
            + "<div style='padding:24px'>"
            + "<h2>Order Cancelled</h2>"
            + "<p>Hi " + order.getCustomerName() + ", your order <strong>#" + order.getId() + "</strong> has been cancelled.</p>"
            + "<p>If you have any questions, please contact us directly.</p>"
            + "</div>"
            + "<div style='background:#f5f5f5;padding:16px;text-align:center;font-size:12px;color:#999'>"
            + "Ember &amp; Oak | 123 Oak Street, New York, NY</div>"
            + "</body></html>";
    }
}
