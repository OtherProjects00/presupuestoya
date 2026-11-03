package com.presupuestoya.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record PublicQuoteView(
        String businessName,
        String ownerName,
        String phone,
        String email,
        String address,
        String taxId,
        String currency,
        String quoteNumber,
        String clientName,
        LocalDate issueDate,
        LocalDate validUntil,
        String statusLabel,
        String statusCss,
        String notes,
        String terms,
        List<Item> items,
        BigDecimal subtotal,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        BigDecimal total) {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String issueDateFormatted() {
        return issueDate == null ? "" : issueDate.format(DATE_FORMAT);
    }

    public String validUntilFormatted() {
        return validUntil == null ? "" : validUntil.format(DATE_FORMAT);
    }

    public boolean hasWhatsapp() {
        return phone != null && !phone.isBlank();
    }

    public String whatsappUrl() {
        if (!hasWhatsapp()) {
            return "#";
        }
        String digits = phone.replaceAll("[^0-9]", "");
        String message = "Hola, quiero consultar por el presupuesto " + quoteNumber;
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8).replace("+", "%20");
        return "https://wa.me/" + digits + "?text=" + encoded;
    }

    public record Item(String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
    }
}
