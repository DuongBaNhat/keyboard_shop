package com.nhat.keyboard_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KeyboardShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeyboardShopApplication.class, args);
	}

}
