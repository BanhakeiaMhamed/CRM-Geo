package com.backend.backend.controllers;

import com.backend.backend.dto.*;
import com.backend.backend.entities.Engineer;
import com.backend.backend.metier.services.ClientService;
import com.backend.backend.metier.services.CurrentUserService;
import com.backend.backend.metier.services.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<ClientDto> list() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return clientService.listByEnterprise(engineer.getEnterprise().getId());
    }

    @PostMapping("/particulier")
    public ClientDto createParticulier(@RequestBody CreateParticulierRequest request) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return clientService.createParticulier(engineer, request);
    }

    @PostMapping("/profesionnel")
    public ClientDto createProfesionnel(@RequestBody CreateProfesionnelRequest request) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return clientService.createProfesionnel(engineer, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        clientService.delete(id, engineer.getEnterprise().getId());
    }
}
