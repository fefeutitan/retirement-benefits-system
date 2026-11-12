# System Design — Retirement Benefits System

Você pode explicar assim:

> O sistema é uma aplicação para gerenciamento de processos de aposentadoria. O frontend Angular conversa com uma API Spring Boot, responsável pelas regras de negócio, validação, persistência e tramitação dos benefícios.

## Arquitetura

```text
Angular
   ↓
Spring Boot REST API
   ├── PostgreSQL
   ├── S3 para PDFs
   └── SNS
         ↓
       SQS
         ↓
  Consumidor idempotente
         ↓
       DLQ
```

## Decisões principais

- **PostgreSQL** armazena benefícios, servidores, documentos e tramitações.
- **S3** armazena o conteúdo dos PDFs; o banco mantém apenas metadados e a chave do objeto.
- **SNS** publica eventos como `BeneficioCriado`.
- **SQS** desacopla o processamento assíncrono.
- **DLQ** recebe mensagens que falharam após três tentativas.
- **Idempotência** evita processar duas vezes o mesmo evento, usando um `eventId` único armazenado no banco.
- **Docker Compose + LocalStack** simulam PostgreSQL, S3, SNS e SQS localmente.
- **Terraform** descreve a infraestrutura AWS real.
- **IAM** fornece permissões mínimas para S3, SNS e SQS.

## Como explicar uma criação de benefício

1. O usuário envia os dados pelo Angular.
2. A API valida e persiste o benefício.
3. Um evento `BeneficioCriado` é publicado no SNS.
4. O SNS entrega a mensagem para a fila SQS.
5. O consumidor processa a mensagem.
6. O `eventId` é registrado para evitar duplicidade.
7. Se houver falhas repetidas, a mensagem vai para a DLQ.

## Trade-offs importantes

Você pode mencionar:

- SNS/SQS foi escolhido por ser integrado à AWS e adequado para processamento assíncrono.
- S3 foi escolhido para evitar armazenar PDFs grandes no PostgreSQL.
- O SQS oferece entrega “pelo menos uma vez”, por isso a idempotência é necessária.
- O LocalStack permite desenvolvimento sem custo AWS.
- Terraform evita configuração manual e torna a infraestrutura reproduzível.

## Se perguntarem sobre limitações

Seja transparente:

> A implementação atual demonstra o fluxo principal. Para produção, eu adicionaria Outbox Pattern para garantir a publicação confiável dos eventos, autenticação, observabilidade com CloudWatch, políticas de retenção do S3 e deploy em ECS/Fargate.

Também deixe claro que Kafka e RabbitMQ estão no roadmap, não são parte do fluxo principal implementado.
