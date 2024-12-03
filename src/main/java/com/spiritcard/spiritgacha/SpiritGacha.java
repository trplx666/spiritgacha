package com.spiritcard.spiritgacha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpiritGacha {
	public static void main(String[] args) {
		SpringApplication.run(SpiritGacha.class, args);
	}
}
