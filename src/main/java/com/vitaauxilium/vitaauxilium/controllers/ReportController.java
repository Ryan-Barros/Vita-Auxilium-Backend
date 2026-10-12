package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/report/environment/activity/{id}")
    public ResponseEntity<?> getActivityReportByEnvironmentId(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/report/environment/sensorial/{id}")
    public ResponseEntity<?> getSensorialReportByEnvironmentId(@PathVariable UUID id) {
        return null;
    }
}