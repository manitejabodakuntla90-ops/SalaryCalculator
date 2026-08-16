package com.example.salary.controller;

import com.example.salary.service.PayCalculatorService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {
    private final PayCalculatorService calculatorService;
    public SalaryController(PayCalculatorService calculatorService) { this.calculatorService = calculatorService; }

    @PostMapping("/calculate")
    public PayCalculatorService.CalculationResult calculate(@RequestBody SalaryRequest request) {
        return calculatorService.calculateRetirementPay(
            LocalDate.parse(request.joinDate), LocalDate.parse(request.retirementDate),
            request.initialLevel, request.initialStage, request.promotions
        );
    }
    public static class SalaryRequest {
        public String joinDate, retirementDate;
        public int initialLevel, initialStage;
        public List<PayCalculatorService.Promotion> promotions;
    }
}
