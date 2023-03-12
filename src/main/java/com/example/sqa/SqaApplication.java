package com.example.sqa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}, scanBasePackages = {
		"com.example.sqa"})
public class SqaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqaApplication.class, args);
	}

}
