package cs203t10.ryver.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RyverBankServiceRegistryApplication {
	public static void main(String[] args) {
		SpringApplication.run(RyverBankServiceRegistryApplication.class, args);
	}
}
