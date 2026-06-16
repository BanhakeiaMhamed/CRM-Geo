package com.backend.backend.dto;

import com.backend.backend.Enums.ReportStatus;

public record TransitionRequest(ReportStatus targetStatus) {}
