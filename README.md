# Benefícios Online — Teste técnico SEPLAG/COTEC

Projeto de portfólio desenvolvido para um **teste de seleção da SEPLAG/COTEC**, com foco em **benefícios de aposentadoria** e implementação em Java/Spring Boot e Angular.

> **Esclarecimento:** Este repositório **não corresponde ao sistema homônimo “Benefícios onLine” desenvolvido profissionalmente na COGERH**. São projetos distintos, com contextos e finalidades diferentes.

Aplicação com backend Spring Boot e frontend Angular.

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
