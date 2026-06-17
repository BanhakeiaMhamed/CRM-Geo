package com.backend.backend.dto;

import org.springframework.core.io.Resource;

public record ReportPdfDownload(String fileName, Resource resource) {}
