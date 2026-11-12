# AWS, armazenamento e mensageria local

O projeto demonstra armazenamento de documentos com S3 e integração assíncrona usando AWS SNS e SQS. No ambiente Docker Compose, os serviços são executados pelo LocalStack para evitar dependência de uma conta AWS durante o desenvolvimento.

Quando `APP_AWS_ENABLED=true`, os PDFs são armazenados no bucket `benefit-documents` e o PostgreSQL guarda apenas a chave do objeto. No perfil padrão, o conteúdo permanece no H2 para facilitar o desenvolvimento sem infraestrutura externa.

## Fluxo

```text
POST /api/beneficios
        |
        v
Spring Boot publica BeneficioCriado
        |
        v
SNS topic: benefit-events
        |
        v
SQS queue: benefit-events-queue
        |
        v
Consumidor agendado do backend processa e remove a mensagem
        |
        v
Após 3 falhas: benefit-events-dlq
```

O SNS permite fan-out para múltiplos consumidores. A SQS fornece retenção, retry e processamento desacoplado. A mensagem só é removida depois de ser processada com sucesso; em caso de erro, permanece disponível para uma nova tentativa.

A fila principal possui uma redrive policy com `maxReceiveCount=3`. Se o consumidor falhar três vezes e não apagar a mensagem, a mensagem é encaminhada automaticamente para `benefit-events-dlq`, evitando retry infinito na fila principal.

## Executar localmente

```bash
docker compose up --build
```

O script `localstack/init-aws.sh` cria automaticamente o tópico, a fila e a assinatura SNS → SQS.

Serviços:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- LocalStack: `http://localhost:4566` (S3, SNS e SQS)
- PostgreSQL: `localhost:5432`

Para executar o backend sem AWS/LocalStack, use o perfil padrão. Nesse modo, `APP_AWS_ENABLED` é `false` e os eventos são ignorados pelo publicador no-op.

## AWS real

Em AWS, substitua `AWS_ENDPOINT_URL` pelo endpoint padrão, configure `AWS_SNS_TOPIC_ARN` e `AWS_SQS_QUEUE_URL`, e forneça uma IAM role com permissões mínimas para `sns:Publish`, `sqs:ReceiveMessage`, `sqs:DeleteMessage` e `sqs:GetQueueAttributes`. As credenciais não devem ser gravadas no Compose ou no código.
