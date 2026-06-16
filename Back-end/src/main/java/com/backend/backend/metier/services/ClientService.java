package com.backend.backend.metier.services;

import com.backend.backend.Enums.ClientType;
import com.backend.backend.dto.ClientDto;
import com.backend.backend.dto.CreateParticulierRequest;
import com.backend.backend.dto.CreateProfesionnelRequest;
import com.backend.backend.entities.Client;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Particulier;
import com.backend.backend.entities.Profesionnel;
import com.backend.backend.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<ClientDto> listByEnterprise(Long enterpriseId) {
        return clientRepository.findByEnterpriseId(enterpriseId).stream()
                .map(ClientDto::from)
                .toList();
    }

    @Transactional
    public ClientDto createParticulier(Engineer engineer, CreateParticulierRequest request) {
        Particulier client = new Particulier();
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setClientType(ClientType.PARTICULIER);
        client.setEngineer(engineer);
        return ClientDto.from(clientRepository.save(client));
    }

    @Transactional
    public ClientDto createProfesionnel(Engineer engineer, CreateProfesionnelRequest request) {
        Profesionnel client = new Profesionnel();
        client.setCompanyName(request.companyName());
        client.setSiren(request.siren());
        client.setSiret(request.siret());
        client.setCompanyType(request.companyType());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setClientType(ClientType.PROFESSIONAL);
        client.setEngineer(engineer);
        return ClientDto.from(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long clientId, Long enterpriseId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));
        if (!client.getEngineer().getEnterprise().getId().equals(enterpriseId)) {
            throw new IllegalArgumentException("Acces refuse");
        }
        clientRepository.delete(client);
    }
}
