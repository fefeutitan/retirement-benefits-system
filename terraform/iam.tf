data "aws_iam_policy_document" "ecs_tasks_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "backend_task" {
  name               = "${var.project_name}-${var.environment}-backend-task"
  assume_role_policy = data.aws_iam_policy_document.ecs_tasks_assume_role.json
}

data "aws_iam_policy_document" "backend_permissions" {
  statement {
    sid       = "DocumentsInS3"
    effect    = "Allow"
    actions   = ["s3:GetObject", "s3:PutObject"]
    resources = ["${aws_s3_bucket.documents.arn}/*"]
  }

  statement {
    sid       = "PublishBenefitEvents"
    effect    = "Allow"
    actions   = ["sns:Publish"]
    resources = [aws_sns_topic.benefit_events.arn]
  }

  statement {
    sid    = "ConsumeBenefitEvents"
    effect = "Allow"
    actions = [
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
      "sqs:ReceiveMessage"
    ]
    resources = [aws_sqs_queue.benefit_events.arn]
  }
}

resource "aws_iam_role_policy" "backend_permissions" {
  name   = "${var.project_name}-${var.environment}-backend-permissions"
  role   = aws_iam_role.backend_task.id
  policy = data.aws_iam_policy_document.backend_permissions.json
}
