package com.example.aniwhere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AniwhereApplication {

	public static void main(String[] args) {
		SpringApplication.run(AniwhereApplication.class, args);
	}

}
