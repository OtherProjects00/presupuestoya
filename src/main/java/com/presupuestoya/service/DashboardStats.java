package com.presupuestoya.service;

import com.presupuestoya.domain.Quote;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStats(
        long totalQuotes,
        long drafts,
        long sent,
        long accepted,
        long rejected,
        BigDecimal acceptedTotal,
        List<Quote> recentQuotes) {
}
