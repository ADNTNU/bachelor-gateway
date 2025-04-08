package no.ntnu.gr10.bachelor_gateway;

import no.ntnu.gr10.bachelor_gateway.entity.Client;
import no.ntnu.gr10.bachelor_gateway.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BachelorGatewayApplication {

	@Bean
	CommandLineRunner initDatabase(ClientRepository clientRepository) {
		return args -> {
			if(clientRepository.findByClientId("testclient") == null){
				Client client = new Client();
				client.setClientId("testclient");
				client.setSecret("supersecret");
				client.setScopes("READ,WRITE");
				clientRepository.save(client);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BachelorGatewayApplication.class, args);
	}

}
