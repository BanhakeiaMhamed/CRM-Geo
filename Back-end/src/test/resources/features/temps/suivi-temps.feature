# language: fr
@temps @suivi
Fonctionnalité: Suivi du temps investi sur les rapports
  En tant qu'ingénieur
  Je veux enregistrer le temps passé sur chaque rapport
  Afin que le gérant puisse mesurer la charge de travail et la rentabilité

  Contexte:
    Étant donné l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Et l'ingénieur "Jean Dupont" rattaché avec l'email "jean.dupont@batigeo.fr"
    Et un rapport "RAP-TM-001" de type "ETUDE_GEOTECHNIQUE" assigné à "jean.dupont@batigeo.fr"

  Scénario: Saisie de temps sur un rapport en cours
    Quand l'ingénieur "jean.dupont@batigeo.fr" enregistre 3,5 heures sur le rapport "RAP-TM-001" le "2026-06-10"
    Et l'ingénieur "jean.dupont@batigeo.fr" enregistre 2 heures sur le rapport "RAP-TM-001" le "2026-06-11"
    Alors le total d'heures sur le rapport "RAP-TM-001" est de 5,5 heures

  Scénario: Total d'heures sur une période pour un ingénieur
    Étant donné que l'ingénieur "jean.dupont@batigeo.fr" a enregistré 4 heures sur "RAP-TM-001" le "2026-06-01"
    Et que l'ingénieur "jean.dupont@batigeo.fr" a enregistré 6 heures sur "RAP-TM-001" le "2026-06-15"
    Quand je calcule le total d'heures de "jean.dupont@batigeo.fr" entre le "2026-06-01" et le "2026-06-30"
    Alors le total d'heures de "jean.dupont@batigeo.fr" sur la période est de 10 heures
