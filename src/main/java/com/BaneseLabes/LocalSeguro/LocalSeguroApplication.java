package com.BaneseLabes.LocalSeguro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(basePackages = "com.BaneseLabes.LocalSeguro.clients")

@SpringBootApplication
public class LocalSeguroApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LocalSeguroApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
	}

}
