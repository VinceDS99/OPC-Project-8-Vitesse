# Vitesse - Application de Gestion de Candidats RH

## Description ##

**Vitesse** est une application Android moderne conçue pour optimiser la gestion des candidats par les services des ressources humaines. 
L'application permet aux professionnels RH de centraliser et organiser efficacement les informations des candidats tout au long du processus de recrutement.

## Fonctionnalités ##

### Gestion des candidats
-  **Visualisation** : Affichage clair de tous les candidats avec photo, nom, et notes
-  **Recherche** : Filtrage instantané par nom ou prénom
-  **Favoris** : Marquage des candidats prioritaires pour un accès rapide
-  **Détails** : Accès aux informations détaillées de chaque candidat

### Informations candidat
Chaque candidat dispose des champs suivants :
- Photo de profil 
- Nom et prénom
- Numéro de téléphone
- Adresse e-mail
- Date de naissance
- Prétention salariale
- Notes détaillés

### Interface utilisateur
- Support multilingue (Français/Anglais)
- Interface responsive adaptée aux différents appareils
- Barre de recherche avec filtrage en temps réel

## Technologies utilisées ##

### Architecture et Design Pattern
- **MVVM** (Model-View-ViewModel) : Séparation claire des responsabilités
- **LiveData** : Observation réactive des changements de données

### Frameworks et bibliothèques
- **Kotlin** : Langage de programmation moderne et concis
- **Room Database** : Base de données locale SQLite
- **Coroutines** : Gestion asynchrone des opérations
- **RecyclerView** : Affichage optimisé des listes

### Outils de développement
- Android Studio
- Git pour le versioning

## Structure du projet ##

//////

## 📱 Utilisation

### Écran d'accueil
1. Au lancement, l'application charge les candidats depuis la base de données
2. L'onglet "Tous" est affiché par défaut avec la liste complète des candidats

### Recherche de candidats
1. Cliquez sur la barre de recherche en haut de l'écran
2. Saisissez un nom ou prénom
3. La liste se filtre automatiquement en temps réel

### Gestion des favoris
1. Naviguez vers l'onglet "Favoris" pour voir uniquement les candidats marqués
2. Les candidats favoris sont identifiables par leur statut

### Consultation des détails
1. Cliquez sur un candidat dans la liste
2. Consultez toutes les informations détaillées

## 🔄 Données de démonstration

L'application inclut 8 candidats de démonstration pour faciliter les tests :
- Jean DUPONT - Développeur full-stack
- Marie MARTIN - Chef de projet agile
- Thomas BERNARD - Designer UX/UI
- Sophie PETIT - Data scientist ⭐
- Lucas ROBERT - Ingénieur DevOps
- Emma RICHARD - Développeuse frontend ⭐
- Antoine DURAND - Architecte logiciel
- Chloé MOREAU - Ingénieure QA

## 🔮 Fonctionnalités à venir

- [ ] Ajout de nouveaux candidats
- [ ] Modification des informations candidats
- [ ] Suppression de candidats
- [ ] Toggle favoris depuis la liste
- [ ] Écran de détails complet
- [ ] Partage d'informations candidat
- [ ] Export des données en CSV
- [ ] Notifications et rappels
- [ ] Synchronisation cloud

