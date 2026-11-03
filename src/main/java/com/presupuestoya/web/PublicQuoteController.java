package com.presupuestoya.web;

import com.presupuestoya.service.PublicQuoteView;
import com.presupuestoya.service.QuoteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicQuoteController {

    private final QuoteService quoteService;

    public PublicQuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/q/{token}")
    public String publicQuote(@PathVariable String token, Model model, HttpServletResponse response) {
        PublicQuoteView quote = quoteService.findPublic(token);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Robots-Tag", "noindex, nofollow, noarchive");
        model.addAttribute("quote", quote);
        return "public-quote";
    }
}
