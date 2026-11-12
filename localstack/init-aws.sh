#!/bin/sh
set -e

awslocal sns create-topic --name benefit-events
awslocal sqs create-queue --queue-name benefit-events-dlq
awslocal sqs create-queue \
  --queue-name benefit-events-queue \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:sa-east-1:000000000000:benefit-events-dlq\",\"maxReceiveCount\":\"3\"}"}'
awslocal sns subscribe \
  --topic-arn arn:aws:sns:sa-east-1:000000000000:benefit-events \
  --protocol sqs \
  --notification-endpoint arn:aws:sqs:sa-east-1:000000000000:benefit-events-queue
