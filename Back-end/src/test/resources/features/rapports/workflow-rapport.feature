# language: fr
@rapport @workflow @smoke
Fonctionnalité: Workflow de validation des rapports
  En tant qu'ingénieur ou gérant
  Je veux faire évoluer un rapport à travers les étapes du cycle de vie
  Afin de garantir la qualité avant envoi au client

  Contexte:
    Étant donné l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Et l'ingénieur "Jean Dupont" rattaché avec l'email "jean.dupont@batigeo.fr"
    Et un rapport "RAP-2026-001" de type "ETUDE_GEOTECHNIQUE" assigné à "jean.dupont@batigeo.fr"

  Scénario: Parcours nominal — de l'étude à l'envoi
    Étant donné que le rapport "RAP-2026-001" est en statut "EN_COURS"
    Quand l'ingénieur soumet le rapport "RAP-2026-001" pour validation
    Alors le rapport "RAP-2026-001" passe en statut "EN_VALIDATION"
    Quand le gérant valide le rapport "RAP-2026-001"
    Alors le rapport "RAP-2026-001" passe en statut "VALIDE"
    Quand le gérant envoie le rapport "RAP-2026-001" au client
    Alors le rapport "RAP-2026-001" passe en statut "ENVOYE"

  Scénario: Rejet et rectification d'un rapport
    Étant donné que le rapport "RAP-2026-001" est en statut "EN_VALIDATION"
    Quand le gérant demande une rectification sur le rapport "RAP-2026-001"
    Alors le rapport "RAP-2026-001" passe en statut "EN_RECTIFICATION"
    Quand l'ingénieur reprend le rapport "RAP-2026-001" après rectification
    Alors le rapport "RAP-2026-001" passe en statut "EN_COURS"

  Scénario: Transition interdite
    Étant donné que le rapport "RAP-2026-001" est en statut "EN_COURS"
    Quand le gérant tente d'envoyer directement le rapport "RAP-2026-001" au client
    Alors une erreur de transition de workflow est retournée
