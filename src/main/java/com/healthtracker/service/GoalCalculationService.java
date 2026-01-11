package com.healthtracker.service;

import com.healthtracker.dao.ReportDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GoalCalculationService {
    private static final int CALORIES_PER_KG = 7700;
    private static final int DAYS_IN_WEEK = 7;

    private final ReportDAO reportDAO;
    private final WeightLogDAO weightLogDAO;
    private final UserDAO userDAO;

    public GoalCalculationService(ReportDAO reportDAO, WeightLogDAO weightLogDAO, UserDAO userDAO) {
        this.reportDAO = reportDAO;
        this.weightLogDAO = weightLogDAO;
        this.userDAO = userDAO;
    }

    public GoalResult calculateTargetIntakeByWeeklyRate(int userId, BigDecimal weeklyRateKg) {
        User user = userDAO.selectUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }

        int neat = reportDAO.calculateDailyNeatExpenditure(userId);

        BigDecimal totalKcalPerWeek = weeklyRateKg.multiply(new BigDecimal(CALORIES_PER_KG));

        int dailyDeficitOrSurplus = totalKcalPerWeek.divide(new BigDecimal(DAYS_IN_WEEK), 0, RoundingMode.HALF_UP)
                .intValue();

        int targetIntakeKcal = neat + dailyDeficitOrSurplus;


        if (user.getGender() == User.Gender.FEMALE && targetIntakeKcal < 1200) {
            targetIntakeKcal = 1200;
        } else if (user.getGender() == User.Gender.MALE && targetIntakeKcal < 1500) {
            targetIntakeKcal = 1500;
        }


        String goalDescription = "Поддержание веса";
        if (weeklyRateKg.floatValue() < 0) {
            goalDescription = "Похудение";
        } else if (weeklyRateKg.floatValue() > 0) {
            goalDescription = "Набор веса";
        }


        int[] macros = calculateTargetMacros(userId);
        int proteinGrams = macros[0];
        int fatsGrams = macros[1];
        int carbsGrams = macros[2];


        float totalWeightChangeKg = weeklyRateKg.floatValue();

        return new GoalResult(
                targetIntakeKcal,
                dailyDeficitOrSurplus,
                goalDescription,
                totalWeightChangeKg,
                proteinGrams,
                fatsGrams,
                carbsGrams
        );
    }

    private int[] calculateTargetMacros(int userId) {

        final BigDecimal PROTEIN_G_PER_KG = new BigDecimal("1.8");
        final BigDecimal FATS_G_PER_KG = new BigDecimal("1.0");
        final BigDecimal CARBS_G_PER_KG = new BigDecimal("3.6");
        BigDecimal weightKg = weightLogDAO.getAbsoluteLatestWeight(userId);
        if (weightKg == null) {
        User user = userDAO.selectUser(userId);
        weightKg = (user != null) ? user.getStartWeightKg() : new BigDecimal("70");
    }
        BigDecimal proteinGramsBD = weightKg.multiply(PROTEIN_G_PER_KG)
                .setScale(0, RoundingMode.HALF_UP);
        int proteinGrams = proteinGramsBD.intValueExact();

        BigDecimal fatsGramsBD = weightKg.multiply(FATS_G_PER_KG)
                .setScale(0, RoundingMode.HALF_UP);
        int fatsGrams = fatsGramsBD.intValueExact();

        BigDecimal carbsGramsBD = weightKg.multiply(CARBS_G_PER_KG)
                .setScale(0, RoundingMode.HALF_UP);
        int carbsGrams = carbsGramsBD.intValueExact();
        return new int[]{proteinGrams, fatsGrams, carbsGrams};
    }


}