package com.example.sqssender;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
		sqsTemplate.send(queueUrl, message);
		log.info("SQS message sent successfully");
	}
}
