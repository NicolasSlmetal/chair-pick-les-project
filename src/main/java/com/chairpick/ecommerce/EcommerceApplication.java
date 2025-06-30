package com.chairpick.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class EcommerceApplication {

	public static void main(String[] args) {
		System.setProperty("mail.smtp.localaddress", "127.0.0.1");
		System.setProperty("mail.smtp.localhost", "localhost");

		SpringApplication.run(EcommerceApplication.class, args);
	}

}
