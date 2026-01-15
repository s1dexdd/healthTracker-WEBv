package com.healthtracker.service;

import com.healthtracker.dao.ReportDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;
import com.healthtracker.model.FoodLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

public class GoalCalculationService {
    private static final int CALORIES_PER_KG = 7700;
    private static final int DAYS_IN_WEEK = 7;

    private final ReportDAO reportDAO;
    private final WeightLogDAO weightLogDAO;
    private final UserDAO userDAO;
    private final FoodLogDAO foodLogDAO;

    public GoalCalculationService(ReportDAO reportDAO, WeightLogDAO weightLogDAO, UserDAO userDAO, FoodLogDAO foodLogDAO) {
        this.reportDAO = reportDAO;
        this.weightLogDAO = weightLogDAO;
        this.userDAO = userDAO;
        this.foodLogDAO = foodLogDAO;
    }

    public GoalResult calculateTargetIntakeByWeeklyRate(int userId, BigDecimal weeklyRateKg) {
        User user = userDAO.selectUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }

        int tdee = reportDAO.calculateDailyNeatExpenditure(userId);

        BigDecimal totalKcalChangePerWeek = weeklyRateKg.multiply(new BigDecimal(CALORIES_PER_KG));
        int dailyAdjustment = totalKcalChangePerWeek.divide(new BigDecimal(DAYS_IN_WEEK), 0, RoundingMode.HALF_UP).intValue();
        
        int targetIntakeKcal = tdee - dailyAdjustment;

        if (user.getGender() == User.Gender.FEMALE && targetIntakeKcal < 1200) {
            targetIntakeKcal = 1200;
        } else if (user.getGender() == User.Gender.MALE && targetIntakeKcal < 1500) {
            targetIntakeKcal = 1500;
        }

        String goalDescription = (weeklyRateKg.compareTo(BigDecimal.ZERO) == 0) ? "Поддержание веса" : 
                                 (weeklyRateKg.compareTo(BigDecimal.ZERO) > 0) ? "Похудение" : "Набор веса";

        int[] targetMacros = calculateTargetMacrosByCalories(targetIntakeKcal, user);
        
        Date today = new Date(System.currentTimeMillis());
        List<FoodLog> todayLogs = foodLogDAO.getFoodLogsByDate(userId, today);
        
        int currentIntakeKcal = 0;
        BigDecimal currentP = BigDecimal.ZERO;
        BigDecimal currentF = BigDecimal.ZERO;
        BigDecimal currentC = BigDecimal.ZERO;

        for (FoodLog log : todayLogs) {
            currentIntakeKcal += log.getCalories();
            if (log.getProteinG() != null) currentP = currentP.add(log.getProteinG());
            if (log.getFatsG() != null) currentF = currentF.add(log.getFatsG());
            if (log.getCarbsG() != null) currentC = currentC.add(log.getCarbsG());
        }

        GoalResult result = new GoalResult(
                targetIntakeKcal,
                dailyAdjustment,
                goalDescription,
                weeklyRateKg.floatValue(),
                targetMacros[0],
                targetMacros[1],
                targetMacros[2]
        );

        result.setCurrentIntakeKcal(currentIntakeKcal);
        result.setCurrentBurnedKcal(reportDAO.calculateDailyWorkoutExpenditure(userId, today));
        result.setCurrentProteinGrams(currentP.setScale(0, RoundingMode.HALF_UP).intValue());
        result.setCurrentFatsGrams(currentF.setScale(0, RoundingMode.HALF_UP).intValue());
        result.setCurrentCarbsGrams(currentC.setScale(0, RoundingMode.HALF_UP).intValue());

        BigDecimal latestWeight = weightLogDAO.getAbsoluteLatestWeight(userId);
        
        float weightToSet = (latestWeight != null) ? latestWeight.floatValue() : user.getStartWeightKg().floatValue();
        result.setCurrentWeightKg(weightToSet);

        return result;
    }

    private int[] calculateTargetMacrosByCalories(int totalKcal, User user) {
    float weight = user.getStartWeightKg().floatValue();
    int proteinG = Math.round(weight * 1.8f);
    int fatsG = Math.round(weight * 0.9f);

    int caloriesFromProteinAndFats = (proteinG * 4) + (fatsG * 9);
    int remainingKcal = totalKcal - caloriesFromProteinAndFats;
    
    int carbsG = Math.round(remainingKcal / 4.0f);
    int minCarbsG = Math.round(weight * 1.0f);

    if (carbsG < minCarbsG) {
        carbsG = minCarbsG;
    }

    return new int[]{proteinG, fatsG, carbsG};
}
}