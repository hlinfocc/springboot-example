package net.hlinfo.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
SecurityAutoConfiguration.class
})
public class SpringbootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootExampleApplication.class, args);
	}

}
