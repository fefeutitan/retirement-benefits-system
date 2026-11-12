output "documents_bucket_name" {
  value = aws_s3_bucket.documents.bucket
}

output "sns_topic_arn" {
  value = aws_sns_topic.benefit_events.arn
}

output "sqs_queue_url" {
  value = aws_sqs_queue.benefit_events.url
}

output "sqs_dlq_url" {
  value = aws_sqs_queue.benefit_events_dlq.url
}

output "backend_task_role_arn" {
  value = aws_iam_role.backend_task.arn
}
