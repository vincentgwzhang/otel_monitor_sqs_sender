package com.example.sqssender;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.contrib.awsxray.propagator.AwsXrayPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.extension.trace.propagation.OtTracePropagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SqsMessageProducer {

	private static final Logger log = LoggerFactory.getLogger(SqsMessageProducer.class);

	private final SqsTemplate sqsTemplate;
	private final String queueUrl;

	public SqsMessageProducer(SqsTemplate sqsTemplate, @Value("${app.sqs.queue-url:}") String queueUrl) {
		this.sqsTemplate = sqsTemplate;
		this.queueUrl = queueUrl;
	}

	public void send(String message) {
		Assert.hasText(queueUrl, "SQS_QUEUE_URL must be configured");
		log.info("Sending SQS message: {}", message);

		MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);
		OtTracePropagator.getInstance().inject(
				Context.current(),
				messageBuilder,
				(carrier, key, value) -> carrier.setHeader(key, value));

		sqsTemplate.send(queueUrl, messageBuilder.build());
		log.info("SQS message sent successfully");
	}

	public void sendWithJaeger(String message) {
		Assert.hasText(queueUrl, "SQS_QUEUE_URL must be configured");
		log.info("Sending SQS message: {}", message);

		MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);
		JaegerPropagator.getInstance().inject(
				Context.current(),
				messageBuilder,
				(carrier, key, value) -> carrier.setHeader(key, value));

		sqsTemplate.send(queueUrl, messageBuilder.build());
		log.info("SQS message sent successfully");
	}

	public void sendWithXRay(String message) {
		Assert.hasText(queueUrl, "SQS_QUEUE_URL must be configured");
		log.info("Sending SQS message: {}", message);

		MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);
		AwsXrayPropagator.getInstance().inject(
				Context.current(),
				messageBuilder,
				(carrier, key, value) -> carrier.setHeader(key, value));

		sqsTemplate.send(queueUrl, messageBuilder.build());
		log.info("SQS message sent successfully");
	}

	public void sendWithB3(String message) {
		Assert.hasText(queueUrl, "SQS_QUEUE_URL must be configured");
		log.info("Sending SQS message: {}", message);

		MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);
		B3Propagator.injectingSingleHeader().inject(
				Context.current(),
				messageBuilder,
				(carrier, key, value) -> carrier.setHeader(key, value));

		sqsTemplate.send(queueUrl, messageBuilder.build());
		log.info("SQS message sent successfully");
	}

	public void sendWithW3CTraceContextPropagator(String message) {
		Assert.hasText(queueUrl, "SQS_QUEUE_URL must be configured");
		log.info("Sending SQS message: {}", message);

		MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);
		W3CTraceContextPropagator.getInstance().inject(
				Context.current(),
				messageBuilder,
				(carrier, key, value) -> carrier.setHeader(key, value));

		sqsTemplate.send(queueUrl, messageBuilder.build());
		log.info("SQS message sent successfully");
	}
}
