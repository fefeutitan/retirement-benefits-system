# SonarQube

O projeto possui configuração para análise estática de backend e frontend, cobertura de testes Java com JaCoCo e execução local pelo Docker Compose.

## Subir o SonarQube

```bash
docker compose up -d sonarqube
```

Acesse `http://localhost:9000`. No primeiro acesso local, o usuário padrão é `admin` e a senha inicial é `admin`; altere-a quando solicitado.

## Gerar cobertura do backend

```bash
cd backend
./mvnw verify -Pcoverage
```

No Windows:

```powershell
cd backend
./mvnw.cmd verify -Pcoverage
```

O relatório XML do JaCoCo é gerado em `backend/target/site/jacoco/jacoco.xml`.

## Executar a análise

Com o SonarScanner CLI instalado na máquina:

```bash
sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=SEU_TOKEN
```

O token deve ser fornecido por variável de ambiente ou pelo shell. Não grave tokens no repositório.

Para um ambiente compartilhado, o mesmo projeto pode usar SonarQube Cloud ou um SonarQube Server externo, configurando `sonar.host.url` e `sonar.token` no CI.
