package com.presupuestoya.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_quotes_number", columnNames = "number"),
                @UniqueConstraint(name = "uk_quotes_public_token", columnNames = "public_token_hash")
        })
public class Quote extends BaseEntity {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, length = 30)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate validUntil;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 2000)
    private String notes;

    @Column(length = 2000)
    private String terms;

    @Column(length = 64)
    private String publicTokenHash;

    private Instant publicTokenCreatedAt;
    private Instant publicTokenExpiresAt;
    private Instant sentAt;
    private Instant acceptedAt;
    private Instant rejectedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quote_id", nullable = false)
    @OrderBy("position ASC")
    private List<QuoteItem> items = new ArrayList<>();

    protected Quote() {
    }

    public Quote(AppUser owner, Client client, String number, LocalDate issueDate, LocalDate validUntil) {
        this.owner = owner;
        this.client = client;
        this.number = number;
        this.issueDate = issueDate;
        this.validUntil = validUntil;
    }

    public void addItem(QuoteItem item) {
        item.setPosition(items.size());
        items.add(item);
    }

    public void recalculateTotals() {
        BigDecimal calculatedSubtotal = items.stream()
                .map(QuoteItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal rate = taxRate == null ? BigDecimal.ZERO : taxRate;
        BigDecimal calculatedTax = calculatedSubtotal
                .multiply(rate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        this.subtotal = calculatedSubtotal;
        this.taxAmount = calculatedTax;
        this.total = calculatedSubtotal.add(calculatedTax).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isDraft() {
        return status == QuoteStatus.DRAFT;
    }

    public boolean isSent() {
        return status == QuoteStatus.SENT;
    }

    public boolean isAccepted() {
        return status == QuoteStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return status == QuoteStatus.REJECTED;
    }

    public boolean isExpired() {
        return status == QuoteStatus.SENT && validUntil != null && validUntil.isBefore(LocalDate.now());
    }

    public String getDisplayStatusLabel() {
        return isExpired() ? "Vencido" : status.getLabel();
    }

    public String getDisplayStatusCss() {
        return isExpired() ? "badge-expired" : status.getCssClass();
    }

    public boolean isPublicLinkActive() {
        return publicTokenHash != null
                && publicTokenExpiresAt != null
                && publicTokenExpiresAt.isAfter(Instant.now());
    }

    public String getIssueDateFormatted() {
        return issueDate == null ? "" : issueDate.format(DATE_FORMAT);
    }

    public String getValidUntilFormatted() {
        return validUntil == null ? "" : validUntil.format(DATE_FORMAT);
    }

    public String getPublicLinkExpiresAtFormatted() {
        if (publicTokenExpiresAt == null) {
            return "";
        }
        return publicTokenExpiresAt.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMAT);
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getTotal() {
        return total;
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

    public String getPublicTokenHash() {
        return publicTokenHash;
    }

    public void setPublicTokenHash(String publicTokenHash) {
        this.publicTokenHash = publicTokenHash;
    }

    public Instant getPublicTokenCreatedAt() {
        return publicTokenCreatedAt;
    }

    public void setPublicTokenCreatedAt(Instant publicTokenCreatedAt) {
        this.publicTokenCreatedAt = publicTokenCreatedAt;
    }

    public Instant getPublicTokenExpiresAt() {
        return publicTokenExpiresAt;
    }

    public void setPublicTokenExpiresAt(Instant publicTokenExpiresAt) {
        this.publicTokenExpiresAt = publicTokenExpiresAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(Instant rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public List<QuoteItem> getItems() {
        return items;
    }

    public void setItems(List<QuoteItem> items) {
        this.items = items;
    }
}
