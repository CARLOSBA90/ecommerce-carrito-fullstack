package com.ecommerce.carrito.scheduler;

import com.ecommerce.carrito.service.TierEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TierUpdateScheduler {

    private final TierEvaluationService tierEvaluationService;

    /**
     * Runs daily at 00:00 to evaluate and update customer tiers
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void evaluateTiers() {
        log.info("Starting scheduled tier evaluation");
        long startTime = System.currentTimeMillis();

        try {
            int updatedCount = tierEvaluationService.evaluateAllCustomerTiers();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Scheduled tier evaluation completed in {}ms. {} customers updated", duration, updatedCount);
        } catch (Exception e) {
            log.error("Error during scheduled tier evaluation", e);
        }
    }
}
