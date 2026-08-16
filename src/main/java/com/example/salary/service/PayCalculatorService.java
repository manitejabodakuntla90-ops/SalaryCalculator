package com.example.salary.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayCalculatorService {

    // 7th CPC Pay Matrix mockup: rows represent stages (1 to max), columns represent levels (1 to max)
    private final int[][] payMatrix = {
        {18000, 19900, 21700, 25500, 29200, 35400, 44900, 47600, 53100, 56100},
        {18500, 20500, 22400, 26300, 30100, 36500, 46200, 49000, 54700, 57800},
        {19100, 21100, 23100, 27100, 31000, 37600, 47600, 50500, 56300, 59500},
        {19700, 21700, 23800, 27900, 31900, 38700, 49000, 52000, 58000, 61300},
        {20300, 22400, 24500, 28700, 32900, 39900, 50500, 53600, 59700, 63100}
    };

    public static class CareerEvent {
        public int year;
        public int level;
        public int stage;
        public int monthlyBasic;
        public int annualTotal;

        public CareerEvent(int year, int level, int stage, int monthlyBasic, int annualTotal) {
            this.year = year;
            this.level = level;
            this.stage = stage;
            this.monthlyBasic = monthlyBasic;
            this.annualTotal = annualTotal;
        }
    }

    public static class CalculationResult {
        public long totalLifetimeEarnings;
        public List<CareerEvent> yearlyProgression;

        public CalculationResult(long totalLifetimeEarnings, List<CareerEvent> yearlyProgression) {
            this.totalLifetimeEarnings = totalLifetimeEarnings;
            this.yearlyProgression = yearlyProgression;
        }
    }

    public static class Promotion {
        public int year;
        public int newLevel;
    }

    public CalculationResult calculateRetirementPay(LocalDate joinDate, LocalDate retirementDate, int initialLevel, int initialStage, List<Promotion> promotions) {
        long totalEarnings = 0;
        List<CareerEvent> progression = new ArrayList<>();

        int currentLevel = initialLevel;
        int currentStage = initialStage;

        int startYear = joinDate.getYear();
        int endYear = retirementDate.getYear();
        int maxStages = payMatrix.length;

        for (int year = startYear; year <= endYear; year++) {
            // Check if there's a promotion this year
            Promotion promo = findPromotionForYear(promotions, year);
            if (promo != null) {
                currentLevel = promo.newLevel;
                // Find matching or next higher stage in the new level corresponding to current basic pay
                int currentSalary = payMatrix[currentStage - 1][currentLevel - 1];
                currentStage = findNextOrEqualStage(currentLevel, currentSalary);
            } else if (year > startYear) {
                // Regular annual increment: increment stage by 1 if not already at max stage
                if (currentStage < maxStages) {
                    currentStage++;
                }
            }

            int monthlyBasic = payMatrix[currentStage - 1][currentLevel - 1];
            int annualTotal = monthlyBasic * 12;
            totalEarnings += annualTotal;

            progression.add(new CareerEvent(year, currentLevel, currentStage, monthlyBasic, annualTotal));
        }

        return new CalculationResult(totalEarnings, progression);
    }

    private Promotion findPromotionForYear(List<Promotion> promotions, int year) {
        if (promotions == null) return null;
        for (Promotion p : promotions) {
            if (p.year == year) return p;
        }
        return null;
    }

    private int findNextOrEqualStage(int level, int currentSalary) {
        for (int stage = 1; stage <= payMatrix.length; stage++) {
            if (payMatrix[stage - 1][level - 1] >= currentSalary) {
                return stage;
            }
        }
        return payMatrix.length;
    }
}
