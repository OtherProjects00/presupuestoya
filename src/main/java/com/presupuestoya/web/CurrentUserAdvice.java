package com.presupuestoya.web;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.service.CurrentUserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    private final CurrentUserService currentUserService;

    public CurrentUserAdvice(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @ModelAttribute("currentUser")
    public AppUser currentUser() {
        return currentUserService.currentUser().orElse(null);
    }
}
