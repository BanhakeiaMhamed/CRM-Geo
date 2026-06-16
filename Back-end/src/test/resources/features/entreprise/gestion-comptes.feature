# language: fr
@entreprise @smoke
Fonctionnalité: Gestion du compte entreprise et des ingénieurs
  En tant qu'administrateur du bureau d'étude
  Je veux créer un compte entreprise et y rattacher des ingénieurs
  Afin que chaque ingénieur accède uniquement aux données de son entreprise

  Contexte:
    Étant donné qu'aucune entreprise n'existe dans le système

  Scénario: Création d'une entreprise et rattachement d'un ingénieur
    Quand je crée l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Et j'y rattache l'ingénieur "Jean Dupont" avec l'email "jean.dupont@batigeo.fr"
    Alors l'entreprise "Bati Geo Études" existe dans le système
    Et l'ingénieur "jean.dupont@batigeo.fr" est rattaché à l'entreprise "Bati Geo Études"

  Scénario: Deux ingénieurs rattachés à la même entreprise
    Étant donné l'entreprise "Bati Geo Études" avec le SIRET "12345678901234"
    Quand j'y rattache l'ingénieur "Jean Dupont" avec l'email "jean.dupont@batigeo.fr"
    Et j'y rattache l'ingénieur "Marie Martin" avec l'email "marie.martin@batigeo.fr"
    Alors l'entreprise "Bati Geo Études" compte 2 ingénieurs rattachés
