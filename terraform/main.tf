resource "aws_s3_bucket" "documents" {
  bucket = var.documents_bucket_name
}

resource "aws_s3_bucket_public_access_block" "documents" {
  bucket = aws_s3_bucket.documents.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "documents" {
  bucket = aws_s3_bucket.documents.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_versioning" "documents" {
  bucket = aws_s3_bucket.documents.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_sns_topic" "benefit_events" {
  name = "benefit-events"
}

resource "aws_sqs_queue" "benefit_events_dlq" {
  name                      = "benefit-events-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "benefit_events" {
  name                       = "benefit-events"
  receive_wait_time_seconds  = 10
  visibility_timeout_seconds = 60

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.benefit_events_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "benefit_events_dlq" {
  queue_url = aws_sqs_queue.benefit_events_dlq.id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns   = [aws_sqs_queue.benefit_events.arn]
  })
}

resource "aws_sns_topic_subscription" "benefit_events" {
  topic_arn = aws_sns_topic.benefit_events.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.benefit_events.arn
}

data "aws_iam_policy_document" "sqs_from_sns" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["sns.amazonaws.com"]
    }

    actions   = ["sqs:SendMessage"]
    resources = [aws_sqs_queue.benefit_events.arn]

    condition {
      test     = "ArnEquals"
      variable = "aws:SourceArn"
      values   = [aws_sns_topic.benefit_events.arn]
    }
  }
}

resource "aws_sqs_queue_policy" "benefit_events" {
  queue_url = aws_sqs_queue.benefit_events.id
  policy    = data.aws_iam_policy_document.sqs_from_sns.json
}
