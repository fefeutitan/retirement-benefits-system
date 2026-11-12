variable "project_name" {
  type    = string
  default = "retirement-benefits-system"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "aws_region" {
  type    = string
  default = "sa-east-1"
}

variable "documents_bucket_name" {
  type        = string
  description = "Globally unique private S3 bucket name for retirement documents."
}
