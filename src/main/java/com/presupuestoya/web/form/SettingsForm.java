package com.presupuestoya.web.form;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class SettingsForm {

    @NotBlank(message = "El nombre del negocio es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres.")
    private String businessName;

    @Size(max = 80, message = "El nombre del responsable no puede superar 80 caracteres.")
    private String ownerName;

    @Size(max = 40, message = "El teléfono no puede superar 40 caracteres.")
    private String phone;

    @Email(message = "Ingresá un email válido.")
    @Size(max = 254, message = "El email es demasiado largo.")
    private String email;

    @Size(max = 250, message = "La dirección no puede superar 250 caracteres.")
    private String address;

    @Size(max = 40, message = "El CUIT/CUIT no puede superar 40 caracteres.")
    private String taxId;

    @NotBlank(message = "La moneda es obligatoria.")
    @Pattern(regexp = "[A-Za-z]{3}", message = "Usá un código de 3 letras, por ejemplo ARS, USD o EUR.")
    private String currency = "ARS";

    @NotNull(message = "El IVA por defecto es obligatorio.")
    @DecimalMin(value = "0.00", message = "El IVA no puede ser negativo.")
    @DecimalMax(value = "100.00", message = "El IVA no puede superar 100%.")
    private BigDecimal defaultTaxRate = BigDecimal.ZERO;

    @Min(value = 1, message = "La validez mínima es 1 día.")
    @Max(value = 365, message = "La validez máxima es 365 días.")
    private int defaultValidityDays = 15;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getDefaultTaxRate() {
        return defaultTaxRate;
    }

    public void setDefaultTaxRate(BigDecimal defaultTaxRate) {
        this.defaultTaxRate = defaultTaxRate;
    }

    public int getDefaultValidityDays() {
        return defaultValidityDays;
    }

    public void setDefaultValidityDays(int defaultValidityDays) {
        this.defaultValidityDays = defaultValidityDays;
    }
}
