# CRM System

## Overview

This project is a CRM (Customer Relationship Management) system built with **Spring Boot** and **Angular**. It manages users, including engineers, HR, and directors, as well as their respective clients, who can either be individuals or professional entities.

## Fonctionnalités

- User Management (CRUD for Engineers, HR, Directors)
- Client Management (CRUD for Individuals and Professionals)
- Project Management
- Quote Management
- User authentication via Firebase (Angular + Firebase Admin SDK)
- CI/CD with Kubernetes (planned)

## Technologies Utilisées

- **Backend:** Spring Boot, Correto-Java 17.0.10, Hibernate, JPA, Lombok  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
  ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
  ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

- **Frontend:** Angular 17  
  ![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)

- **Database:** PostgreSQL 16  
  ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)

- **CI/CD:** Docker, Kubernetes (planned)  
  ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
  ![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)

- **Authentication:** Firebase Authentication + Firebase Admin SDK  
  ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

## Installation

### Prerequis

- Java 17 or later
- Node.js and npm
- PostgreSQL
- Maven
- Compte Firebase (Console Firebase)

### Configuration Firebase

#### Backend (Firebase Admin SDK)

1. Console Firebase > Parametres du projet > Comptes de service > Generer une nouvelle cle privee
2. Copier le JSON telecharge vers `Back-end/src/main/resources/firebase-service-account.json`
   (ou definir `FIREBASE_CREDENTIALS_PATH` vers le chemin du fichier)
3. Voir le modele : `Back-end/src/main/resources/firebase-service-account.json.example`

#### Frontend (Firebase Auth client)

1. Console Firebase > Parametres du projet > Vos applications > Config SDK
2. Renseigner les valeurs dans `front-end/src/environments/environment.ts` :

```typescript
firebase: {
  apiKey: '...',
  authDomain: '...',
  projectId: '...',
  storageBucket: '...',
  messagingSenderId: '...',
  appId: '...'
}
```

3. Activer **Email/Password** dans Firebase Authentication > Sign-in method

#### Roles utilisateur (custom claims)

Le backend lit le claim Firebase `role` (ex: `INGENIEUR`, `GERANT`, `ADMIN`) pour autoriser les routes securisees.

### Backend Setup

1. **Clone the Repository**

   ```sh
   git clone https://github.com/BanhakeiaMhamed/CRM-Geo
   cd CRM-Geo/Back-end
1. **Installation de jenkins**

   ```sh
   run this commands in your cmd windows:
   1- docker pull jenkins/jenkins
   2-docker run -u jenkins-CRM -p 8080:8080 -p 50000:50000 -v jenkins_crm:/var/jankins_home -v /var/run/docker.sock:/var/run/docker.sock
