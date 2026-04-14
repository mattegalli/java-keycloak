# Personal Notes — Spring Boot + Keycloak

A full-stack Java web application that integrates with Keycloak for identity and access management. It covers the full authentication lifecycle — from browser login to JWT validation on the backend — and includes custom Keycloak extensions built with the SPI framework.

## What it does

Users can create, read, and delete personal notes. Each user only sees their own notes. Admins can view everyone's notes. Authentication is handled entirely by Keycloak.

## Tech stack

| Layer          | Technology                             |
| -------------- | -------------------------------------- |
| Backend        | Spring Boot 3.3, Java 17               |
| Security       | Spring Security OAuth2 Resource Server |
| Identity       | Keycloak 25                            |
| Frontend       | Vanilla JS, Keycloak.js                |
| Build          | Maven Wrapper                          |
| Infrastructure | Docker Compose                         |

## Architecture

```
Browser
  │
  ├── keycloak.js (Authorization Code + PKCE)
  │       │
  │       └── Keycloak (localhost:8180)
  │               ├── notes-realm
  │               ├── realm roles: user, admin
  │               └── SPI extensions (providers/)
  │
  └── fetch() with Bearer token
          │
          └── Spring Boot (localhost:8080)
                  ├── JWT validation (JWKS auto-discovery)
                  ├── Role-based access (@PreAuthorize)
                  └── REST API (/api/notes)
```

## Keycloak SPI Extensions

The `keycloak-extensions` module extends Keycloak directly via the Service Provider Interface:

**Audit Event Listener** — hooks into Keycloak's event system and logs every login, failed login, logout, and admin operation. Useful for audit trails and security monitoring.

**Account Approval Authenticator** — adds a custom step to the authentication flow that checks whether a user has an `account.approved = true` attribute before allowing login. Demonstrates how to gate access at the identity layer rather than the application layer.

## Getting started

**Prerequisites:** Java 17, Docker, Docker Compose

```bash
# 1. Start Keycloak (realm, clients, roles and test users are imported automatically)
docker compose up -d

# 2. Start the app
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
./mvnw spring-boot:run

# 3. Open the browser
open http://localhost:8080
```

Login with one of the pre-configured test users:

| Username | Password | Role        |
| -------- | -------- | ----------- |
| alice    | password | user        |
| bob      | password | user        |
| admin    | admin    | user, admin |

## Building the SPI extensions

```bash
cd keycloak-extensions
../mvnw package -DskipTests
cp target/keycloak-extensions-*.jar ../keycloak/providers/
docker compose restart keycloak
```

Then enable the extensions in the Keycloak Admin Console (`http://localhost:8180`):

- **Audit Event Listener:** Realm Settings → Events → Event Listeners → add `audit-event-listener`
- **Account Approval Authenticator:** Authentication → create a flow, add the `Account Approval Check` step after `Username Password Form`, bind it as the browser flow

## Project structure

```
├── src/                        Spring Boot app
│   └── main/
│       ├── java/com/example/notes/
│       │   ├── config/         SecurityConfig (JWT, CORS)
│       │   ├── controller/     REST endpoints
│       │   ├── model/          Note entity
│       │   └── service/        In-memory store
│       └── resources/
│           ├── static/         index.html (frontend)
│           └── application.properties
├── keycloak-extensions/        Keycloak SPI module
│   └── src/main/java/com/example/spi/
│       ├── event/              Audit Event Listener
│       └── authenticator/      Account Approval Authenticator
├── keycloak/
│   ├── realm-export.json       Realm config (auto-imported on boot)
│   └── providers/              Drop SPI JARs here
└── docker-compose.yml
```

## Notes

- Data is stored in memory — restarting the app clears all notes. That is intentional; persistence is not the focus here.
- Keycloak runs with `start-dev` and an embedded H2 database. Use `docker compose stop` (not `down`) to preserve realm data between restarts.
- The realm export is committed to the repo so the entire setup is reproducible from a single `docker compose up`.
