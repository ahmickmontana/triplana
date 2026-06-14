package com.triplana.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("Password1!"));
		SpringApplication.run(BackendApplication.class, args);
	}

}
