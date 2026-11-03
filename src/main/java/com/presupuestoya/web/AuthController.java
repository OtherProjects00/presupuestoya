package com.presupuestoya.web;

import com.presupuestoya.config.AppProperties;
import com.presupuestoya.service.UserService;
import com.presupuestoya.web.form.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final AppProperties appProperties;

    public AuthController(UserService userService, AppProperties appProperties) {
        this.userService = userService;
        this.appProperties = appProperties;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/app";
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!appProperties.isRegistrationEnabled()) {
            return "redirect:/login?registrationDisabled";
        }
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding,
                           RedirectAttributes redirectAttributes) {
        if (!appProperties.isRegistrationEnabled()) {
            return "redirect:/login?registrationDisabled";
        }
        if (binding.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada. Ya podés ingresar.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            binding.reject("formError", exception.getMessage());
            return "register";
        }
    }
}
