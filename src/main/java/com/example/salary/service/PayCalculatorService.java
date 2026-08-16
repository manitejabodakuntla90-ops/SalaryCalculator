package com.example.salary.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayCalculatorService {

    // Expanded 7th CPC Pay Matrix mock (20 stages, 10 levels) to prevent premature capping
    private final int[][] payMatrix = {
        {18000, 19900, 21700, 25500, 29200, 35400, 44900, 47600, 53100, 56100},
        {18500, 20500, 22400, 26300, 30100, 36500, 46200, 49000, 54700, 57800},
        {19100, 21100, 23100, 27100, 31000, 37600, 47600, 50500, 56300, 59500},
        {19700, 21700, 23800, 27900, 31900, 38700, 49000, 52000, 58000, 61300},
        {20300, 22400, 24500, 28700, 32900, 39900, 50500, 53600, 59700, 63100},
        {20900, 23100, 25200, 29600, 33900, 41100, 52000, 55200, 61500, 65000},
        {21500, 23800, 26000, 30500, 34900, 42300, 53600, 56900, 63300, 67000},
        {22100, 24500, 26800, 31400, 35900, 43600, 55200, 58600, 65200, 69000},
        {22800, 25200, 27600, 32300, 37000, 44900, 56900, 60400, 67200, 71100},
        {23500, 26000, 28400, 33300, 38100, 46200, 58600, 62200, 69200, 73200},
        {24200, 26800, 29300, 34300, 39200, 47600, 60400, 64100, 71300, 75400},
        {24900, 27600, 30200, 35300, 40400, 49000, 62200, 66000, 73400, 77700},
        {25600, 28400, 31100, 36400, 41600, 50500, 64100, 68000, 75600, 80000},
        {26400, 29300, 32000, 37500, 42800, 52000, 66000, 70000, 77900, 82400},
        {27200, 30200, 33000, 38600, 44100, 53600, 68000, 72100, 80200, 84900},
        {28000, 31100, 34000, 39800, 45400, 55200, 70000, 74300, 82600, 87400},
        {28800, 32000, 35000, 41000, 46800, 56900, 72100, 76500, 85100, 90000},
        {29700, 33000, 36100, 42200, 48200, 58600, 74300, 78800, 87700, 92700},
        {30600, 34000, 37200, 43500, 49600, 60400, 76500, 81200, 90300, 95500},
        {31500, 35000, 38300, 44800, 51100, 62200, 78800, 83600, 93000, 98400}
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
            Promotion promo = findPromotionForYear(promotions, year);
            if (promo != null) {
                currentLevel = promo.newLevel;
                int currentSalary = payMatrix[currentStage - 1][currentLevel - 1];
                currentStage = findNextOrEqualStage(currentLevel, currentSalary);
            } else if (year > startYear) {
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
