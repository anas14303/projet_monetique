# Paiement App SB

Application de gestion des paiements avec Spring Boot et Thymeleaf.

## Fonctionnalités

- Gestion des utilisateurs (CRUD)
- Interface utilisateur moderne avec Bootstrap 5
- Pagination et recherche
- Gestion des erreurs personnalisée
- Base de données PostgreSQL

## Prérequis

- Java 17 ou supérieur
- Maven 3.6.3 ou supérieur
- PostgreSQL 13 ou supérieur
- Un navigateur web moderne

## Installation

1. Clonez le dépôt :
   ```bash
   git clone [URL_DU_DEPOT]
   cd paiement-appsb
   ```

2. Créez une base de données PostgreSQL nommée `paiement_monetique`

3. Configurez les identifiants de la base de données dans `src/main/resources/application.properties`

4. Compilez et exécutez l'application :
   ```bash
   mvn spring-boot:run
   ```

5. Accédez à l'application à l'adresse : [http://localhost:8080](http://localhost:8080)

## Comptes par défaut

- **Admin** : admin@example.com / (mot de passe à définir)
- **Utilisateur** : user@example.com / (mot de passe à définir)

## Structure du projet

```
paiement-appsb/
├── src/
│   ├── main/
│   │   ├── java/com/monetique/paiement_appsb/
│   │   │   ├── config/       # Configurations Spring
│   │   │   ├── controller/   # Contrôleurs
│   │   │   ├── model/        # Entités JPA
│   │   │   ├── repository/   # Répertoires de données
│   │   │   ├── service/      # Couche de service
│   │   │   └── PaiementAppSbApplication.java
│   │   └── resources/
│   │       ├── static/        # Fichiers statiques (CSS, JS, images)
│   │       ├── templates/    # Vues Thymeleaf
│   │       └── application.properties
│   └── test/                 # Tests unitaires et d'intégration
└── pom.xml
```

## Technologies utilisées

- **Backend** :
  - Spring Boot 3.1.3
  - Spring Data JPA
  - Hibernate
  - PostgreSQL

- **Frontend** :
  - Thymeleaf
  - Bootstrap 5.2.3
  - jQuery 3.6.0
  - Font Awesome 6.0.0

- **Outils** :
  - Maven
  - Lombok
  - H2 Database (pour les tests)

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.
