package com.backend.backend.Enums;

/**
 * Roles applicatifs (claims Firebase custom: role).
 * INGENIEUR : tableau Kanban personnel, saisie du temps.
 * GERANT   : validation des rapports, dashboards KPI.
 * RH       : gestion des comptes ingénieurs.
 * ADMIN    : administration du compte entreprise.
 */
public enum Role {
    INGENIEUR,
    GERANT,
    RH,
    ADMIN
}
