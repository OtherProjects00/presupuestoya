package com.presupuestoya.web;

import com.presupuestoya.config.AppProperties;
import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.Quote;
import com.presupuestoya.domain.QuoteStatus;
import com.presupuestoya.service.BusinessProfileService;
import com.presupuestoya.service.ClientService;
import com.presupuestoya.service.CurrentUserService;
import com.presupuestoya.service.QuoteService;
import com.presupuestoya.web.form.QuoteForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
public class QuoteController {

    private final CurrentUserService currentUserService;
    private final QuoteService quoteService;
    private final ClientService clientService;
    private final BusinessProfileService profileService;
    private final AppProperties appProperties;

    public QuoteController(CurrentUserService currentUserService,
                           QuoteService quoteService,
                           ClientService clientService,
                           BusinessProfileService profileService,
                           AppProperties appProperties) {
        this.currentUserService = currentUserService;
        this.quoteService = quoteService;
        this.clientService = clientService;
        this.profileService = profileService;
        this.appProperties = appProperties;
    }

    @GetMapping("/app/quotes")
    public String list(@RequestParam(required = false) String status, Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        QuoteStatus filter = parseStatus(status);
        List<Quote> quotes = quoteService.list(user, filter);
        model.addAttribute("quotes", quotes);
        model.addAttribute("selectedStatus", filter == null ? "" : filter.name());
        model.addAttribute("statuses", QuoteStatus.values());
        model.addAttribute("profile", profileService.get(user));
        return "quotes/list";
    }

    @GetMapping("/app/quotes/new")
    public String createForm(@RequestParam(required = false) Long clientId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        if (clientService.list(user).isEmpty()) {
            return "redirect:/app/clients/new?forQuote=true";
        }
        var profile = profileService.get(user);
        QuoteForm form = new QuoteForm();
        if (clientId != null) {
            form.setClientId(clientService.get(user, clientId).getId());
        }
        form.setIssueDate(LocalDate.now());
        form.setValidUntil(LocalDate.now().plusDays(profile.getDefaultValidityDays()));
        form.setTaxRate(profile.getDefaultTaxRate());
        model.addAttribute("quoteForm", form);
        model.addAttribute("clients", clientService.list(user));
        model.addAttribute("profile", profileService.get(user));
        return "quotes/form";
    }

    @GetMapping("/app/quotes/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        Quote quote = quoteService.getDetailed(user, id);
        if (!quote.isDraft()) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden editar borradores.");
            return "redirect:/app/quotes/" + id;
        }
        model.addAttribute("quoteForm", quoteService.toForm(quote));
        model.addAttribute("clients", clientService.list(user));
        model.addAttribute("profile", profileService.get(user));
        return "quotes/form";
    }

    @PostMapping("/app/quotes/save")
    public String save(@Valid @ModelAttribute("quoteForm") QuoteForm form,
                       BindingResult binding,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        if (binding.hasErrors()) {
            model.addAttribute("clients", clientService.list(user));
            model.addAttribute("profile", profileService.get(user));
            return "quotes/form";
        }
        try {
            Quote quote;
            if (form.getId() == null) {
                quote = quoteService.create(user, form);
                redirectAttributes.addFlashAttribute("success", "Presupuesto creado.");
            } else {
                quote = quoteService.update(user, form.getId(), form);
                redirectAttributes.addFlashAttribute("success", "Presupuesto actualizado.");
            }
            return "redirect:/app/quotes/" + quote.getId();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            binding.reject("formError", exception.getMessage());
            model.addAttribute("clients", clientService.list(user));
            model.addAttribute("profile", profileService.get(user));
            return "quotes/form";
        }
    }

    @GetMapping("/app/quotes/{id}")
    public String detail(@PathVariable Long id, Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        Quote quote = quoteService.getDetailed(user, id);
        model.addAttribute("quote", quote);
        model.addAttribute("profile", profileService.get(user));
        return "quotes/detail";
    }

    @GetMapping("/app/quotes/{id}/print")
    public String print(@PathVariable Long id, Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        Quote quote = quoteService.getDetailed(user, id);
        model.addAttribute("quote", quote);
        model.addAttribute("profile", profileService.get(user));
        return "quotes/print";
    }

    @PostMapping("/app/quotes/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        QuoteStatus target = parseStatus(status);
        if (target == null) {
            redirectAttributes.addFlashAttribute("error", "Estado inválido.");
            return "redirect:/app/quotes/" + id;
        }
        try {
            quoteService.markStatus(user, id, target);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/app/quotes/" + id;
    }

    @PostMapping("/app/quotes/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        try {
            quoteService.delete(user, id);
            redirectAttributes.addFlashAttribute("success", "Presupuesto borrado.");
            return "redirect:/app/quotes";
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/app/quotes/" + id;
        }
    }

    @PostMapping("/app/quotes/{id}/share")
    public String share(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        String token = quoteService.createPublicLink(user, id);
        String baseUrl = appProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            baseUrl = "http://localhost:8080";
        }
        baseUrl = baseUrl.replaceAll("/+$", "");
        redirectAttributes.addFlashAttribute("publicUrl", baseUrl + "/q/" + token);
        redirectAttributes.addFlashAttribute("success", "Enlace público generado. Copialo ahora: no se vuelve a mostrar.");
        return "redirect:/app/quotes/" + id;
    }

    @PostMapping("/app/quotes/{id}/share/revoke")
    public String revokeShare(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        quoteService.revokePublicLink(user, id);
        redirectAttributes.addFlashAttribute("success", "Enlace público revocado.");
        return "redirect:/app/quotes/" + id;
    }

    private QuoteStatus parseStatus(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return QuoteStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
