# J-Stream 

J-Stream est une application desktop de streaming multimédia développée en JavaFX, inspirée de plateformes comme Netflix et Prime Video.

##  Fonctionnalités

###  Module Utilisateur
- Inscription et authentification sécurisée
- Recherche de films et séries
- Lecture vidéo MP4
- Gestion des favoris
- Historique de visionnage
- Système de notation 
- Commentaires et avis
- Gestion des saisons et épisodes
- Reprise intelligente des vidéos

###  Module Administrateur
- Gestion complète du catalogue (CRUD)
- Gestion des catégories
- Gestion des séries, saisons et épisodes
- Modération des commentaires
- Dashboard analytique avec graphiques

---

## Architecture du Projet

Le projet respecte une architecture en 5 couches :

1. **View**
   - Interfaces JavaFX (.fxml)
   - Feuilles CSS

2. **Controller**
   - Gestion des événements utilisateur

3. **Service**
   - Logique métier

4. **DAO**
   - Accès aux données et requêtes SQL

5. **Model**
   - Entités Java (POJO)

---

##  Technologies Utilisées

- Java 17
- JavaFX
- MySQL 
- JDBC
- Maven
- BCrypt
- MaterialFX / ControlsFX

