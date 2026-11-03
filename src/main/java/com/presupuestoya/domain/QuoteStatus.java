package com.presupuestoya.domain;

public enum QuoteStatus {

    DRAFT("Borrador", "badge-draft"),
    SENT("Enviado", "badge-sent"),
    ACCEPTED("Aceptado", "badge-accepted"),
    REJECTED("Rechazado", "badge-rejected");

    private final String label;
    private final String cssClass;

    QuoteStatus(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
