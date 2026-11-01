# Como executar localmente

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd`.

O backend inicia com H2 em memória. Para PostgreSQL:

```bash
SPRING_PROFILES_ACTIVE=prod \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/beneficios \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./mvnw spring-boot:run
```

## Frontend

```bash
cd frontend
npm install
npm start
```

A aplicação fica disponível em `http://localhost:4200`.
