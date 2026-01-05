package com.ecommerce.carrito.controller;

import com.ecommerce.carrito.service.TierEvaluationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backoffice/tiers")
@RequiredArgsConstructor
public class TierManagementController {

    private final TierEvaluationService tierEvaluationService;

    @PostMapping("/evaluate")
    public ResponseEntity<TierEvaluationResponse> triggerTierEvaluation() {
        int updatedCount = tierEvaluationService.evaluateAllCustomerTiers();

        return ResponseEntity.ok(new TierEvaluationResponse(
                "Tier evaluation completed successfully",
                updatedCount));
    }

    @Data
    @AllArgsConstructor
    public static class TierEvaluationResponse {
        private String message;
        private int tiersUpdated;
    }
}
