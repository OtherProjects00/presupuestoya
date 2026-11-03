package com.presupuestoya.web;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.Client;
import com.presupuestoya.service.ClientService;
import com.presupuestoya.service.CurrentUserService;
import com.presupuestoya.web.form.ClientForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {

    private final CurrentUserService currentUserService;
    private final ClientService clientService;

    public ClientController(CurrentUserService currentUserService, ClientService clientService) {
        this.currentUserService = currentUserService;
        this.clientService = clientService;
    }

    @GetMapping("/app/clients")
    public String list(Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        model.addAttribute("clients", clientService.list(user));
        return "clients/list";
    }

    @GetMapping("/app/clients/new")
    public String createForm(Model model) {
        model.addAttribute("clientForm", new ClientForm());
        return "clients/form";
    }

    @GetMapping("/app/clients/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        Client client = clientService.get(user, id);
        model.addAttribute("clientForm", clientService.toForm(client));
        return "clients/form";
    }

    @PostMapping("/app/clients/save")
    public String save(@Valid @ModelAttribute("clientForm") ClientForm form,
                       BindingResult binding,
                       RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return "clients/form";
        }
        AppUser user = currentUserService.requireCurrentUser();
        if (form.getId() == null) {
            clientService.create(user, form);
            redirectAttributes.addFlashAttribute("success", "Cliente creado.");
        } else {
            clientService.update(user, form.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado.");
        }
        return "redirect:/app/clients";
    }

    @PostMapping("/app/clients/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireCurrentUser();
        try {
            clientService.delete(user, id);
            redirectAttributes.addFlashAttribute("success", "Cliente borrado.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/app/clients";
    }
}
