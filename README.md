🚀 Application de Gestion d’Entreprise


📌 Description

Ce projet est une application web développée dans le cadre du Master 2 ISI à l’ESMT.
Elle permet de gérer efficacement les employés, départements, projets et tâches d’une entreprise, avec une interface claire et intuitive.

L’objectif est de fournir une solution centralisée et flexible pour la gestion des ressources internes d’une organisation.


🏗️ Architecture de l’application

1. Backend (Jakarta EE)

Entités JPA : Employee, Department, Project, Task

Services (EJB Stateless) :

DepartmentService (gestion des départements, affectation d’employés, etc.)

EmployeeService

ProjectService

TaskService

Utilisation directe de EntityManager (via @PersistenceContext) pour gérer la persistance.

Pas de couche DAO supplémentaire : la logique métier et l’accès aux données sont regroupés dans les services.


2. Frontend (Thymeleaf + Bootstrap)

Thymeleaf pour générer les pages dynamiques côté serveur.

Bootstrap pour un design responsive et moderne.

Intégration avec les Servlets qui jouent le rôle de contrôleurs.


3. Base de données (MySQL / PostgreSQL)

Tables principales :

employee

department

project

task



🛠️ Technologies utilisées

Backend : Jakarta EE (Servlets, EJB, EntityManager, Services)

Frontend : Thymeleaf + Bootstrap (HTML, CSS, JS)

Base de données : MySQL ou PostgreSQL

Serveur d’application : Apache Tomcat / GlassFish / Wildfly

IDE : IntelliJ IDEA

📂 Fonctionnalités principales

👨‍💼 Gestion des employés

Ajouter, modifier, supprimer un employé

Afficher la liste des employés

Recherche par critères (nom, département, rôle)

🏢 Gestion des départements

Créer, modifier, supprimer un département

Lister les départements

Assigner des employés à un département

📊 Gestion des projets

Créer et suivre l’avancement des projets

Modifier les détails d’un projet

Lister les projets en cours ou terminés

✅ Gestion des tâches

Créer et assigner des tâches aux employés

Suivre l’avancement des tâches

Modifier ou supprimer une tâche

Visualiser les tâches par employé ou projet

🔐 Fonctionnalités avancées

Tableau de bord avec vue globale (statistiques & graphiques)

Authentification avec rôles (Admin, Manager, Employé)

Exportation de données (PDF/Excel – optionnel)

Notifications lors de l’attribution de tâches/projets
