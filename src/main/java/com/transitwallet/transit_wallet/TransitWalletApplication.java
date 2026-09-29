package com.transitwallet.transit_wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransitWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransitWalletApplication.class, args);
	}

}
