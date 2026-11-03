package com.presupuestoya.service;

import com.presupuestoya.config.AppProperties;
import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.BusinessProfile;
import com.presupuestoya.domain.Client;
import com.presupuestoya.domain.Quote;
import com.presupuestoya.domain.QuoteItem;
import com.presupuestoya.domain.QuoteStatus;
import com.presupuestoya.repository.BusinessProfileRepository;
import com.presupuestoya.repository.ClientRepository;
import com.presupuestoya.repository.QuoteRepository;
import com.presupuestoya.security.TokenService;
import com.presupuestoya.web.form.LineItemForm;
import com.presupuestoya.web.form.QuoteForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteService.class);
    private static final char[] NUMBER_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final QuoteRepository quotes;
    private final ClientRepository clients;
    private final BusinessProfileRepository profiles;
    private final TokenService tokens;
    private final AppProperties appProperties;
    private final SecureRandom random = new SecureRandom();

    public QuoteService(QuoteRepository quotes,
                        ClientRepository clients,
                        BusinessProfileRepository profiles,
                        TokenService tokens,
                        AppProperties appProperties) {
        this.quotes = quotes;
        this.clients = clients;
        this.profiles = profiles;
        this.tokens = tokens;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public List<Quote> list(AppUser owner, QuoteStatus status) {
        if (status == null) {
            return quotes.findByOwnerIdOrderByCreatedAtDesc(owner.getId());
        }
        return quotes.findByOwnerIdAndStatusOrderByCreatedAtDesc(owner.getId(), status);
    }

    @Transactional(readOnly = true)
    public Quote getDetailed(AppUser owner, Long id) {
        return quotes.findDetailedByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new NotFoundException("Presupuesto no encontrado."));
    }

    @Transactional
    public Quote create(AppUser owner, QuoteForm form) {
        Client client = clients.findByIdAndOwnerId(form.getClientId(), owner.getId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));

        Quote quote = new Quote(owner, client, generateNumber(), form.getIssueDate(), form.getValidUntil());
        applyForm(quote, form);
        quote.recalculateTotals();
        Quote saved = quotes.save(quote);
        log.info("Presupuesto creado number={} ownerId={}", saved.getNumber(), owner.getId());
        return saved;
    }

    @Transactional
    public Quote update(AppUser owner, Long id, QuoteForm form) {
        Quote quote = getDetailed(owner, id);
        if (!quote.isDraft()) {
            throw new IllegalStateException("Solo se pueden editar presupuestos en borrador.");
        }
        Client client = clients.findByIdAndOwnerId(form.getClientId(), owner.getId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));
        quote.setClient(client);
        applyForm(quote, form);
        quote.recalculateTotals();
        log.info("Presupuesto actualizado number={} ownerId={}", quote.getNumber(), owner.getId());
        return quote;
    }

    @Transactional
    public void markStatus(AppUser owner, Long id, QuoteStatus target) {
        Quote quote = getDetailed(owner, id);
        if (target == null) {
            throw new IllegalArgumentException("Estado inválido.");
        }
        Instant now = Instant.now();
        switch (quote.getStatus()) {
            case DRAFT -> {
                if (target != QuoteStatus.SENT) {
                    throw new IllegalStateException("Un borrador solo puede marcarse como enviado.");
                }
                quote.setSentAt(now);
            }
            case SENT -> {
                if (target != QuoteStatus.ACCEPTED && target != QuoteStatus.REJECTED) {
                    throw new IllegalStateException("Un presupuesto enviado solo puede aceptarse o rechazarse.");
                }
                if (target == QuoteStatus.ACCEPTED) {
                    quote.setAcceptedAt(now);
                } else {
                    quote.setRejectedAt(now);
                }
            }
            default -> throw new IllegalStateException("El presupuesto ya está cerrado.");
        }
        quote.setStatus(target);
        log.info("Presupuesto {} pasó a estado {} ownerId={}", quote.getNumber(), target, owner.getId());
    }

    @Transactional
    public void delete(AppUser owner, Long id) {
        Quote quote = getDetailed(owner, id);
        if (!quote.isDraft()) {
            throw new IllegalStateException("Solo se pueden borrar presupuestos en borrador.");
        }
        quotes.delete(quote);
        log.info("Presupuesto borrado number={} ownerId={}", quote.getNumber(), owner.getId());
    }

    @Transactional
    public String createPublicLink(AppUser owner, Long id) {
        Quote quote = getDetailed(owner, id);
        String rawToken = tokens.newPublicToken();
        Instant now = Instant.now();
        int days = Math.max(1, appProperties.getPublicQuoteDays());
        quote.setPublicTokenHash(tokens.sha256(rawToken));
        quote.setPublicTokenCreatedAt(now);
        quote.setPublicTokenExpiresAt(now.plus(Duration.ofDays(days)));
        log.info("Enlace público generado number={} ownerId={}", quote.getNumber(), owner.getId());
        return rawToken;
    }

    @Transactional
    public void revokePublicLink(AppUser owner, Long id) {
        Quote quote = getDetailed(owner, id);
        quote.setPublicTokenHash(null);
        quote.setPublicTokenCreatedAt(null);
        quote.setPublicTokenExpiresAt(null);
        log.info("Enlace público revocado number={} ownerId={}", quote.getNumber(), owner.getId());
    }

    @Transactional(readOnly = true)
    public PublicQuoteView findPublic(String token) {
        if (token == null || token.isBlank() || token.length() > 200) {
            throw new NotFoundException("Presupuesto no encontrado.");
        }
        Quote quote = quotes.findPublicByTokenHash(tokens.sha256(token))
                .orElseThrow(() -> new NotFoundException("Presupuesto no encontrado."));
        if (quote.getPublicTokenExpiresAt() == null || !quote.getPublicTokenExpiresAt().isAfter(Instant.now())) {
            throw new NotFoundException("Este enlace venció.");
        }
        BusinessProfile profile = profiles.findByUserId(quote.getOwner().getId())
                .orElseThrow(() -> new NotFoundException("Perfil de negocio no encontrado."));

        List<PublicQuoteView.Item> items = quote.getItems().stream()
                .map(item -> new PublicQuoteView.Item(
                        item.getDescription(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .toList();

        return new PublicQuoteView(
                profile.getBusinessName(),
                profile.getOwnerName(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getTaxId(),
                profile.getCurrency(),
                quote.getNumber(),
                quote.getClient().getName(),
                quote.getIssueDate(),
                quote.getValidUntil(),
                quote.getDisplayStatusLabel(),
                quote.getDisplayStatusCss(),
                quote.getNotes(),
                quote.getTerms(),
                items,
                quote.getSubtotal(),
                quote.getTaxRate(),
                quote.getTaxAmount(),
                quote.getTotal());
    }

    @Transactional(readOnly = true)
    public DashboardStats dashboard(AppUser owner) {
        long total = quotes.countByOwnerId(owner.getId());
        long drafts = quotes.countByOwnerIdAndStatus(owner.getId(), QuoteStatus.DRAFT);
        long sent = quotes.countByOwnerIdAndStatus(owner.getId(), QuoteStatus.SENT);
        long accepted = quotes.countByOwnerIdAndStatus(owner.getId(), QuoteStatus.ACCEPTED);
        long rejected = quotes.countByOwnerIdAndStatus(owner.getId(), QuoteStatus.REJECTED);
        BigDecimal acceptedTotal = quotes.sumTotalByOwnerIdAndStatus(owner.getId(), QuoteStatus.ACCEPTED);
        List<Quote> recent = quotes.findTop5ByOwnerIdOrderByCreatedAtDesc(owner.getId());
        return new DashboardStats(total, drafts, sent, accepted, rejected,
                acceptedTotal == null ? BigDecimal.ZERO : acceptedTotal, recent);
    }

    public QuoteForm toForm(Quote quote) {
        QuoteForm form = new QuoteForm();
        form.setId(quote.getId());
        form.setClientId(quote.getClient().getId());
        form.setIssueDate(quote.getIssueDate());
        form.setValidUntil(quote.getValidUntil());
        form.setTaxRate(quote.getTaxRate());
        form.setNotes(quote.getNotes());
        form.setTerms(quote.getTerms());

        List<LineItemForm> items = new ArrayList<>();
        for (QuoteItem item : quote.getItems()) {
            LineItemForm line = new LineItemForm();
            line.setDescription(item.getDescription());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());
            items.add(line);
        }
        while (items.size() < 3) {
            items.add(new LineItemForm());
        }
        form.setItems(items);
        return form;
    }

    private void applyForm(Quote quote, QuoteForm form) {
        if (form.getIssueDate() == null || form.getValidUntil() == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        }
        if (form.getValidUntil().isBefore(form.getIssueDate())) {
            throw new IllegalArgumentException("La validez no puede ser anterior a la emisión.");
        }
        quote.setIssueDate(form.getIssueDate());
        quote.setValidUntil(form.getValidUntil());
        quote.setTaxRate(form.getTaxRate() == null
                ? BigDecimal.ZERO
                : form.getTaxRate().setScale(2, RoundingMode.HALF_UP));
        quote.setNotes(clean(form.getNotes()));
        quote.setTerms(clean(form.getTerms()));

        List<QuoteItem> newItems = buildItems(form);
        quote.getItems().clear();
        quote.getItems().addAll(newItems);
    }

    private List<QuoteItem> buildItems(QuoteForm form) {
        List<QuoteItem> items = new ArrayList<>();
        if (form.getItems() == null) {
            throw new IllegalArgumentException("Agregá al menos un ítem.");
        }
        for (LineItemForm item : form.getItems()) {
            if (item == null) {
                continue;
            }
            boolean blank = !StringUtils.hasText(item.getDescription()) && item.getUnitPrice() == null;
            if (blank) {
                continue;
            }
            if (!StringUtils.hasText(item.getDescription())) {
                throw new IllegalArgumentException("Cada ítem necesita una descripción.");
            }
            if (item.getQuantity() == null || item.getQuantity().signum() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo.");
            }
            items.add(new QuoteItem(
                    items.size(),
                    item.getDescription().trim(),
                    item.getQuantity().setScale(2, RoundingMode.HALF_UP),
                    item.getUnitPrice().setScale(2, RoundingMode.HALF_UP)));
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Agregá al menos un ítem.");
        }
        return items;
    }

    private String generateNumber() {
        int year = LocalDate.now().getYear();
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder candidate = new StringBuilder("P-").append(year).append('-');
            for (int i = 0; i < 8; i++) {
                candidate.append(NUMBER_ALPHABET[random.nextInt(NUMBER_ALPHABET.length)]);
            }
            String number = candidate.toString();
            if (!quotes.existsByNumber(number)) {
                return number;
            }
        }
        throw new IllegalStateException("No se pudo generar un número de presupuesto único.");
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
