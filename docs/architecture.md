# Arquitetura

O projeto é organizado como um monorepo:

```text
beneficios_online/
├── backend/     # API REST Java 21 + Spring Boot
├── frontend/    # SPA Angular 21
└── docs/        # Documentação local
```

## Backend

O backend usa Spring MVC, Spring Data JPA, Bean Validation, Flyway e Springdoc OpenAPI. O banco padrão é H2 em memória. O perfil `prod` utiliza PostgreSQL.

Endpoints iniciais:

- `GET /api/status`
- `GET /v3/api-docs`
- `GET /swagger-ui.html`

## Frontend

O frontend usa componentes standalone, Angular Router, Signals e Reactive Forms. Os testes unitários usam Vitest e os testes end-to-end usam Playwright com Chromium.
