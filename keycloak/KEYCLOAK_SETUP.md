# Keycloak Setup

`realm-export.json` is automatically imported by Keycloak on first boot (via `--import-realm`).
It creates everything needed for the learning project:

| What              | Detail                                                                                                      |
| ----------------- | ----------------------------------------------------------------------------------------------------------- |
| Realm             | `notes-realm`                                                                                               |
| Client (backend)  | `notes-backend` — bearer-only, used by Spring Boot to validate JWTs                                         |
| Client (frontend) | `notes-frontend` — public client, PKCE enabled, redirect to `http://localhost:8080/*`                       |
| Realm roles       | `user`, `admin`                                                                                             |
| Test users        | `alice` / `password` (role: user), `bob` / `password` (role: user), `admin` / `admin` (roles: user + admin) |

---

## Start Keycloak

```bash
docker compose up -d
```

---

## Admin Console

Open: http://localhost:8180/admin/master/console/
Login: `admin` / `admin`

Switch to the `notes-realm` realm using the dropdown in the top-left corner.

---

## Stop (keep data) vs teardown

```bash
docker compose stop     # stops container
docker compose start    # restarts it

docker compose down     # removes container
```

---
