package com.syncura360;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Syncura360 Spring application.
 */
 @SpringBootApplication
@RequiredArgsConstructor
public class Syncura360SpringApplication {
	public static void main(String[] args) {
		
		SpringApplication.run(Syncura360SpringApplication.class, args);
	}
}
