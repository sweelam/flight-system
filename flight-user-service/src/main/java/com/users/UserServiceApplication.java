package com.users;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${management.tracing.url}") String url) {
		return OtlpHttpSpanExporter.builder()
				.setEndpoint(url)
				.build();
	}

}
