package com.backend.backend.dto;

import com.backend.backend.Enums.ClientType;
import com.backend.backend.entities.Client;
import com.backend.backend.entities.Particulier;
import com.backend.backend.entities.Profesionnel;

public record ClientDto(
        Long id,
        ClientType clientType,
        String email,
        String phone,
        String displayName,
        String firstName,
        String lastName,
        String companyName,
        String siret,
        String engineerEmail
) {
    public static ClientDto from(Client client) {
        if (client instanceof Particulier p) {
            return new ClientDto(
                    client.getId(),
                    client.getClientType(),
                    client.getEmail(),
                    client.getPhone(),
                    p.getFirstName() + " " + p.getLastName(),
                    p.getFirstName(),
                    p.getLastName(),
                    null,
                    null,
                    client.getEngineer() != null ? client.getEngineer().getEmail() : null
            );
        }
        if (client instanceof Profesionnel pro) {
            return new ClientDto(
                    client.getId(),
                    client.getClientType(),
                    client.getEmail(),
                    client.getPhone(),
                    pro.getCompanyName(),
                    null,
                    null,
                    pro.getCompanyName(),
                    pro.getSiret(),
                    client.getEngineer() != null ? client.getEngineer().getEmail() : null
            );
        }
        return new ClientDto(
                client.getId(),
                client.getClientType(),
                client.getEmail(),
                client.getPhone(),
                client.getEmail(),
                null,
                null,
                null,
                null,
                null
        );
    }
}
