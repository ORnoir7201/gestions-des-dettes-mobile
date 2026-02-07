# Carnet de Dettes - Application de Gestion de Crédits

##  Sujet du Projet
**Carnet de Dettes** est une application Android complète de gestion de crédits et de dettes, permettant aux commerçants et particuliers de suivre efficacement leurs transactions financières avec leurs clients.

###  Fonctionnalités principales
- **Gestion des clients** (ajout, modification, suppression)
- **Suivi des dettes** avec calcul automatique des soldes
- **Enregistrement des paiements**
- **Tableau de bord** avec statistiques
- **Historique complet** des transactions
- **Authentification sécurisée**
- **Interface moderne et intuitive**

---

##  Membres du Groupe
- **OUEDRAOGO ROMAIN**
- **MONE DANIEL**
- **KONATE STANISLAS**

---

##  Architecture Choisie

### Architecture Technique
L'application suit une architecture **MVVM (Model-View-ViewModel)** qui repose sur une **architecture Android modulaire en couches**, permettant une séparation claire des responsabilités entre l’interface utilisateur, la logique métier et la gestion des données distantes

### Technologique
- **Langage** : Java
- **SDK Minimum** : Android API 24 (Android 7.0)
- **Architecture** : MVVM
- **Base de données** : Supabase (PostgreSQL)
- **API** : REST avec Retrofit
- **Authentification** : Supabase Auth
- **UI** : Material Design Components

---

## Installation et Configuration

### Prérequis
1. **Android Studio** (version 2022.3.1 ou supérieure)
2. **JDK 17** ou supérieur
3. **Compte GitHub** pour cloner le projet
4. **Compte Supabase** pour la base de données

### Étape 1 : Cloner le projet
```bash
git clone https://github.com/votre-username/carnet-dettes.git
cd carnet-dettes
```
### Étape 2 : Ouvrir dans Android Studio
1. Lancez Android Studio
2. Sélectionnez "Open an Existing Project"
3. Naviguez vers le dossier du projet cloné
4. Attendez la synchronisation Gradle
### Étape 3 : Configuration de Supabase
1. Créez un compte sur Supabase
2. Créez un nouveau projet
3. Récupérez votre URL et API Key
4. Exécutez le script SQL fourni dans l'éditeur SQL
### Étape 4 : Configuration de l'application
1. Ouvrez le fichier ``` SupabaseService.java```
2. Remplacez les valeurs par défaut par vos identifiants Supabase
### Étape 5 : Build et Exécution
1. Clean Project : ``` Build > Clean Project```
2. Rebuild Project : ``` Build > Rebuild Project```
3. Exécuter : Cliquez sur le bouton Run 

