package com.presupuestoya.web.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuoteForm {

    private Long id;

    @NotNull(message = "Seleccioná un cliente.")
    private Long clientId;

    @NotNull(message = "La fecha de emisión es obligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate = LocalDate.now();

    @NotNull(message = "La fecha de validez es obligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validUntil = LocalDate.now().plusDays(15);

    @NotNull(message = "El IVA es obligatorio.")
    @DecimalMin(value = "0.00", message = "El IVA no puede ser negativo.")
    @DecimalMax(value = "100.00", message = "El IVA no puede superar 100%.")
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Size(max = 2000, message = "Las observaciones no pueden superar 2000 caracteres.")
    private String notes;

    @Size(max = 2000, message = "Las condiciones no pueden superar 2000 caracteres.")
    private String terms;

    @Valid
    private List<LineItemForm> items = new ArrayList<>();

    public QuoteForm() {
        for (int i = 0; i < 3; i++) {
            items.add(new LineItemForm());
        }
    }

    @AssertTrue(message = "Agregá al menos un ítem con descripción, cantidad y precio.")
    public boolean isItemsValid() {
        if (items == null) {
            return false;
        }
        boolean hasItem = false;
        for (LineItemForm item : items) {
            if (item == null) {
                continue;
            }
            boolean blank = (item.getDescription() == null || item.getDescription().isBlank())
                    && item.getUnitPrice() == null;
            if (blank) {
                continue;
            }
            hasItem = true;
            if (item.getDescription() == null || item.getDescription().isBlank()) {
                return false;
            }
            if (item.getQuantity() == null || item.getQuantity().signum() <= 0) {
                return false;
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() < 0) {
                return false;
            }
        }
        return hasItem;
    }

    @AssertTrue(message = "La validez no puede ser anterior a la fecha de emisión.")
    public boolean isDateRangeValid() {
        return issueDate == null || validUntil == null || !validUntil.isBefore(issueDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public List<LineItemForm> getItems() {
        return items;
    }

    public void setItems(List<LineItemForm> items) {
        this.items = items;
    }
}
