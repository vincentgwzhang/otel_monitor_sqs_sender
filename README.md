# Purpose
This is v2 version, change for:
1. Detail write down the meta infor for every SQS message
2. Tried different propagator (in lunch.json)
3. Dynamic use cusomize source code

# SQS producer tracing PoC

This Spring Boot application publishes a string to a real AWS SQS queue through
Spring Cloud AWS `SqsTemplate`. Tracing comes exclusively from the upstream
OpenTelemetry Java agent; the application does not create or inject trace
headers.

## Configuration

Set these environment variables before starting the application:

```bash
export AWS_REGION=eu-west-1
export SQS_QUEUE_URL=https://sqs.eu-west-1.amazonaws.com/123456789012/my-queue
export OTEL_SERVICE_NAME=sqs-producer
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
```

AWS credentials are resolved by the standard AWS SDK credential provider
chain. For example, use `AWS_PROFILE`, AWS credential environment variables,
or the task/instance role. Never put credentials in this project or image.

`SQS_QUEUE_URL` must be a queue URL, not an ARN. The caller needs
`sqs:SendMessage` permission for that queue.

## Run the JAR

Download the standard agent and build the application:

```bash
curl -L -o opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.28.1/opentelemetry-javaagent.jar
./mvnw clean package
java -javaagent:./opentelemetry-javaagent.jar -jar target/sqssender-0.0.1-SNAPSHOT.jar
```

## Run with Docker

```bash
docker build -t sqs-producer .
docker run --rm -p 8080:8080 \
  -e AWS_REGION -e SQS_QUEUE_URL \
  -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e AWS_SESSION_TOKEN \
  -e OTEL_SERVICE_NAME -e OTEL_TRACES_EXPORTER \
  -e OTEL_EXPORTER_OTLP_ENDPOINT -e OTEL_METRICS_EXPORTER -e OTEL_LOGS_EXPORTER \
  sqs-producer
```

For local Docker execution the example forwards temporary AWS credential
environment variables. In container platforms, omit them and use the
platform's workload/task role.

## Send and verify

```bash
curl -i -X POST \
  "http://localhost:8080/messages?message=otel-sqs-test"
```

A successful request returns HTTP 202 and logs the payload followed by send
success. In the tracing backend, verify an HTTP server span with an AWS SDK SQS
send span beneath it. Context propagation is deliberately left entirely to the
OpenTelemetry agent's AWS SDK instrumentation, including its SQS propagation
mechanism; there are no application-created `traceparent`, `AWSTraceHeader`, or
message attributes.