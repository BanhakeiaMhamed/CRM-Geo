package com.backend.backend.controllers;

import com.backend.backend.dto.AttachEngineerRequest;
import com.backend.backend.dto.EngineerSummaryDto;
import com.backend.backend.entities.Engineer;
import com.backend.backend.metier.services.CurrentUserService;
import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.repositories.IEngineerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engineers")
@RequiredArgsConstructor
public class EngineerController {

    private final IEngineerRepository engineerRepository;
    private final CurrentUserService currentUserService;
    private final EnterpriseService enterpriseService;

    @GetMapping
    public List<EngineerSummaryDto> list() {
        Engineer current = currentUserService.getCurrentEngineer();
        return engineerRepository.findByEnterpriseId(current.getEnterprise().getId()).stream()
                .map(EngineerSummaryDto::from)
                .toList();
    }

    @PostMapping
    public EngineerSummaryDto attach(@RequestBody AttachEngineerRequest request) {
        Engineer current = currentUserService.getCurrentEngineer();
        String userName = request.email().split("@")[0];
        Engineer engineer = enterpriseService.attachEngineer(
                current.getEnterprise(),
                request.firstName(),
                request.lastName(),
                request.email(),
                userName
        );
        return EngineerSummaryDto.from(engineer);
    }
}
