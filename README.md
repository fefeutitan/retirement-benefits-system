# beneficios_online

Aplicação de benefícios online com backend Spring Boot e frontend Angular.

## Estrutura

- `backend`: API REST Java 21 / Spring Boot.
- `frontend`: aplicação Angular.

## Desenvolvimento

Backend:

```bash
cd backend
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm start
```

Por padrão, o backend usa H2 em memória. O perfil `prod` usa PostgreSQL; configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` antes de iniciar.
