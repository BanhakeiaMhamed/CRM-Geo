package com.backend.backend.exceptions;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String reference) {
        super("Rapport introuvable : " + reference);
    }
}
