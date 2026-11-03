package com.presupuestoya.web;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.BusinessProfile;
import com.presupuestoya.service.BusinessProfileService;
import com.presupuestoya.service.CurrentUserService;
import com.presupuestoya.web.form.SettingsForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

    private final CurrentUserService currentUserService;
    private final BusinessProfileService profileService;

    public SettingsController(CurrentUserService currentUserService, BusinessProfileService profileService) {
        this.currentUserService = currentUserService;
        this.profileService = profileService;
    }

    @GetMapping("/app/settings")
    public String settings(Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        BusinessProfile profile = profileService.get(user);
        model.addAttribute("settingsForm", profileService.toForm(profile));
        return "settings";
    }

    @PostMapping("/app/settings")
    public String save(@Valid @ModelAttribute("settingsForm") SettingsForm form,
                       BindingResult binding,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return "settings";
        }
        AppUser user = currentUserService.requireCurrentUser();
        profileService.update(user, form);
        redirectAttributes.addFlashAttribute("success", "Datos del negocio actualizados.");
        return "redirect:/app/settings";
    }
}
