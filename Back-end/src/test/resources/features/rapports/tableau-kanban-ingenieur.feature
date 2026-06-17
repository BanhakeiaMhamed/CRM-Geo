# language: fr
@rapport @kanban @ingenieur
Fonctionnalité: Tableau Kanban de l'ingénieur (style Jira)
  En tant qu'ingénieur
  Je veux visualiser mes rapports par colonne de statut avec des tags
  Afin de suivre mon travail comme sur un tableau de tickets

  Contexte:
    Étant donné l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Et l'ingénieur "Jean Dupont" rattaché avec l'email "jean.dupont@batigeo.fr"

  Scénario: Rapports répartis sur le tableau Kanban
    Étant donné un rapport "RAP-K-001" de type "G5_DIAGNOSTIC" en statut "EN_COURS" assigné à "jean.dupont@batigeo.fr"
    Et un rapport "RAP-K-002" de type "G2_AVP" en statut "EN_VALIDATION" assigné à "jean.dupont@batigeo.fr"
    Et un rapport "RAP-K-003" de type "G3_PHASE_ETUDE" en statut "EN_RECTIFICATION" assigné à "jean.dupont@batigeo.fr"
    Quand l'ingénieur "jean.dupont@batigeo.fr" consulte son tableau Kanban
    Alors la colonne "EN_COURS" contient 1 rapport
    Et la colonne "EN_VALIDATION" contient 1 rapport
    Et la colonne "EN_RECTIFICATION" contient 1 rapport

  Scénario: Ajout de tags sur un rapport
    Étant donné un rapport "RAP-K-010" de type "G2_PRO" en statut "EN_COURS" assigné à "jean.dupont@batigeo.fr"
    Quand l'ingénieur ajoute le tag "urgent" au rapport "RAP-K-010"
    Et l'ingénieur ajoute le tag "client-vip" au rapport "RAP-K-010"
    Alors le rapport "RAP-K-010" possède les tags "urgent" et "client-vip"
