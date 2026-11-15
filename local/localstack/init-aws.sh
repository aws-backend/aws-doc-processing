#!/usr/bin/env bash

set -euo pipefail

awslocal sqs create-queue \
  --queue-name document-processing-dlq

DLQ_URL=$(awslocal sqs get-queue-url \
  --queue-name document-processing-dlq \
  --query QueueUrl \
  --output text)

DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --query Attributes.QueueArn \
  --output text)

REDRIVE_POLICY=$(cat <<EOF
{
  "deadLetterTargetArn": "$DLQ_ARN",
  "maxReceiveCount": "5"
}
EOF
)

awslocal sqs create-queue \
  --queue-name document-processing \
  --attributes "RedrivePolicy=$REDRIVE_POLICY"

echo "Local SQS queues created"