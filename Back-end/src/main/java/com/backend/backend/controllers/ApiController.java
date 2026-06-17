package com.backend.backend.controllers;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.dto.*;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Report;
import com.backend.backend.metier.services.CurrentUserService;
import com.backend.backend.metier.services.KpiService;
import com.backend.backend.metier.services.ReportWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CurrentUserService currentUserService;
    private final ReportWorkflowService reportWorkflowService;
    private final KpiService kpiService;

    @GetMapping("/me")
    public UserProfileDto me() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return new UserProfileDto(
                engineer.getEmail(),
                engineer.getFirstName(),
                engineer.getLastName(),
                engineer.getRole(),
                engineer.getEnterprise().getName()
        );
    }

    @GetMapping("/reports/kanban")
    public Map<ReportStatus, List<ReportDto>> kanban() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return reportWorkflowService.findKanbanBoardDto(engineer).stream()
                .collect(Collectors.groupingBy(ReportDto::status));
    }

    @GetMapping("/reports")
    public List<ReportDto> myReports() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return reportWorkflowService.findKanbanBoardDto(engineer);
    }

    @PostMapping("/reports")
    public ReportDto createReport(@RequestBody CreateReportRequest request) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        Report report = reportWorkflowService.createReport(
                request.title(),
                request.reference(),
                request.reportType(),
                engineer.getEnterprise(),
                engineer,
                request.revenueAmount() != null ? request.revenueAmount() : java.math.BigDecimal.ZERO,
                request.address(),
                request.postalCode(),
                request.city()
        );
        return ReportDto.from(report);
    }

    @PatchMapping("/reports/{reference}/pages")
    public ReportDto updatePages(@PathVariable String reference,
                                 @RequestBody UpdatePageCountRequest request) {
        Report report = reportWorkflowService.updatePageCount(reference, request.pageCount());
        return ReportDto.from(report);
    }

    @PostMapping(value = "/reports/{reference}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReportDto uploadPdf(@PathVariable String reference,
                               @RequestParam("file") MultipartFile file) {
        Report report = reportWorkflowService.attachPdf(reference, file);
        return ReportDto.from(report);
    }

    @GetMapping("/reports/{reference}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String reference) {
        ReportPdfDownload pdf = reportWorkflowService.loadPdf(reference);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + pdf.fileName().replace("\"", "") + "\"")
                .body(pdf.resource());
    }

    @GetMapping("/interventions/map")
    public List<InterventionMapDto> interventionsMap() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return reportWorkflowService.interventionsMap(engineer.getEnterprise().getId());
    }

    @PatchMapping("/reports/{reference}/transition")
    public ReportDto transition(@PathVariable String reference,
                                @RequestBody TransitionRequest request) {
        Report report = reportWorkflowService.transition(reference, request.targetStatus());
        return ReportDto.from(report);
    }

    @PostMapping("/reports/{reference}/tags/{tag}")
    public ReportDto addTag(@PathVariable String reference, @PathVariable String tag) {
        Report report = reportWorkflowService.addTag(reference, tag);
        return ReportDto.from(report);
    }

    @GetMapping("/dashboard/overview")
    public DashboardOverviewDto overview() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        Long enterpriseId = engineer.getEnterprise().getId();

        Map<ReportStatus, Long> byStatus = new EnumMap<>(ReportStatus.class);
        for (ReportStatus status : ReportStatus.values()) {
            byStatus.put(status, kpiService.countReportsInStatus(enterpriseId, status));
        }
        long total = Arrays.stream(ReportStatus.values())
                .mapToLong(s -> byStatus.getOrDefault(s, 0L))
                .sum();

        return new DashboardOverviewDto(engineer.getEnterprise().getName(), byStatus, total);
    }

    @GetMapping("/dashboard/benefits")
    public DashboardBenefitsDto benefits(
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(required = false) String periodType,
            @RequestParam(required = false) Integer periodNumber) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return kpiService.engineerBenefitsDashboard(
                engineer.getEnterprise().getId(), year, periodType, periodNumber);
    }
}
