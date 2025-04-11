package no.ntnu.gr10.bachelor_gateway.service;

import jakarta.transaction.Transactional;
import no.ntnu.gr10.bachelor_gateway.entity.Client;
import no.ntnu.gr10.bachelor_gateway.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository){
    this.clientRepository = clientRepository;
  }

  public Optional<Client> findByClientId(String clientID){
    return clientRepository.findByClientId(clientID);
  }



}
