package com.ecommerce.carrito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcommerceCarritoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceCarritoApplication.class, args);
	}

}
