package com.presupuestoya.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "business_profiles", uniqueConstraints = @UniqueConstraint(name = "uk_business_profiles_user", columnNames = "user_id"))
public class BusinessProfile extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 120)
    private String businessName;

    @Column(length = 80)
    private String ownerName;

    @Column(length = 40)
    private String phone;

    @Column(length = 254)
    private String email;

    @Column(length = 250)
    private String address;

    @Column(length = 40)
    private String taxId;

    @Column(nullable = false, length = 3)
    private String currency = "ARS";

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultTaxRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private int defaultValidityDays = 15;

    protected BusinessProfile() {
    }

    public BusinessProfile(AppUser user, String businessName) {
        this.user = user;
        this.businessName = businessName;
        this.ownerName = businessName;
        this.email = user.getEmail();
        this.currency = "ARS";
        this.defaultTaxRate = BigDecimal.ZERO;
        this.defaultValidityDays = 15;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

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
