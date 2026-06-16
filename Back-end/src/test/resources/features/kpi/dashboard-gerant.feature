# language: fr
@kpi @dashboard @gerant
Fonctionnalité: Dashboard KPI du gérant
  En tant que gérant du bureau d'étude
  Je veux consulter les indicateurs par ingénieur
  Afin de piloter l'activité et mesurer le bénéfice généré

  Contexte:
    Étant donné l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Et l'ingénieur "Jean Dupont" rattaché avec l'email "jean.dupont@batigeo.fr"
    Et l'ingénieur "Marie Martin" rattaché avec l'email "marie.martin@batigeo.fr"

  Scénario: Suivi de l'activité en cours par statut
    Étant donné un rapport "RAP-D-001" de type "DIAGNOSTIC" en statut "EN_COURS" assigné à "jean.dupont@batigeo.fr"
    Et un rapport "RAP-D-002" de type "EXPERTISE" en statut "EN_VALIDATION" assigné à "marie.martin@batigeo.fr"
    Quand le gérant consulte le dashboard de l'entreprise "Bati Geo Études"
    Alors le dashboard affiche 1 rapport en statut "EN_COURS"
    Et le dashboard affiche 1 rapport en statut "EN_VALIDATION"

  Scénario: Bénéfice mensuel généré par ingénieur
    Étant donné un rapport "RAP-B-001" de type "ETUDE_GEOTECHNIQUE" avec un bénéfice de 5000 euros assigné à "jean.dupont@batigeo.fr"
    Et le rapport "RAP-B-001" a été envoyé au client en juin 2026
    Quand le gérant calcule le bénéfice mensuel de juin 2026 pour "jean.dupont@batigeo.fr"
    Alors le bénéfice mensuel de "jean.dupont@batigeo.fr" pour juin 2026 est de 5000 euros

  Scénario: Bénéfice trimestriel consolidé
    Étant donné un rapport "RAP-T-001" de type "AUDIT_ENERGETIQUE" avec un bénéfice de 3000 euros assigné à "marie.martin@batigeo.fr"
    Et le rapport "RAP-T-001" a été envoyé au client en avril 2026
    Et un rapport "RAP-T-002" de type "DIAGNOSTIC" avec un bénéfice de 2000 euros assigné à "marie.martin@batigeo.fr"
    Et le rapport "RAP-T-002" a été envoyé au client en mai 2026
    Quand le gérant calcule le bénéfice trimestriel T2 2026 pour "marie.martin@batigeo.fr"
    Alors le bénéfice trimestriel de "marie.martin@batigeo.fr" pour T2 2026 est de 5000 euros
