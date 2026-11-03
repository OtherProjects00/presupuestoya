package com.presupuestoya.web.form;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class LineItemForm {

    @Size(max = 200, message = "La descripción no puede superar 200 caracteres.")
    private String description;

    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero.")
    @DecimalMax(value = "999999.00", message = "La cantidad es demasiado grande.")
    private BigDecimal quantity = BigDecimal.ONE;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo.")
    @DecimalMax(value = "99999999.00", message = "El precio es demasiado grande.")
    private BigDecimal unitPrice;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
