# Infraestrutura AWS com Terraform

Este diretório descreve a infraestrutura AWS do sistema:

- Bucket S3 privado e criptografado para PDFs;
- Versionamento do bucket;
- Tópico SNS `benefit-events`;
- Fila SQS principal;
- Dead Letter Queue após três falhas;
- Subscription SNS → SQS;
- Policy da fila permitindo publicação apenas pelo tópico;
- IAM Role para o backend com permissões mínimas de S3, SNS e SQS.

## Uso

```bash
terraform init
terraform fmt -check
terraform validate
terraform plan -var-file=terraform.tfvars
terraform apply -var-file=terraform.tfvars
```

Copie `terraform.tfvars.example` para `terraform.tfvars` e informe um nome globalmente único para o bucket. O arquivo `terraform.tfvars` não deve ser versionado.

O `apply` não faz parte do fluxo local padrão. Para desenvolvimento, use Docker Compose + LocalStack; para AWS real, configure credenciais via AWS CLI, SSO ou role de execução, nunca em arquivos do projeto.
