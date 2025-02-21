package com.example.syncura360_spring;

import com.example.syncura360_spring.restservice.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@RequiredArgsConstructor
public class Syncura360SpringApplication {

	public static void main(String[] args) {

		SpringApplication.run(Syncura360SpringApplication.class, args);
	}

}
