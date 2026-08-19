package com.example.sqssender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

	private final SqsMessageProducer producer;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public MessageController(SqsMessageProducer producer) {
		this.producer = producer;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void send() {
		producer.send("message send time: %s".formatted(LocalDateTime.now().format(formatter)));
	}
}
