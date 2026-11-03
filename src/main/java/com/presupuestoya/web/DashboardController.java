package com.presupuestoya.web;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.service.BusinessProfileService;
import com.presupuestoya.service.ClientService;
import com.presupuestoya.service.CurrentUserService;
import com.presupuestoya.service.QuoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CurrentUserService currentUserService;
    private final QuoteService quoteService;
    private final BusinessProfileService profileService;
    private final ClientService clientService;

    public DashboardController(CurrentUserService currentUserService,
                               QuoteService quoteService,
                               BusinessProfileService profileService,
                               ClientService clientService) {
        this.currentUserService = currentUserService;
        this.quoteService = quoteService;
        this.profileService = profileService;
        this.clientService = clientService;
    }

    @GetMapping("/app")
    public String dashboard(Model model) {
        AppUser user = currentUserService.requireCurrentUser();
        model.addAttribute("stats", quoteService.dashboard(user));
        model.addAttribute("profile", profileService.get(user));
        model.addAttribute("clientsCount", clientService.list(user).size());
        return "dashboard";
    }
}
