package com.healthtracker.dao;

import com.healthtracker.model.User;
import java.sql.Date;
import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportDAO {
    private final FoodLogDAO foodLogDAO;
    private final WorkoutLogDAO workoutLogDAO;
    private final BMRDAO bmrdao;
    private final UserDAO userDAO;
    private final WeightLogDAO weightLogDAO;

    public ReportDAO(FoodLogDAO foodLogDAO, WorkoutLogDAO workoutLogDAO, BMRDAO bmrdao, UserDAO userDAO, WeightLogDAO weightLogDAO) {
        this.foodLogDAO = Objects.requireNonNull(foodLogDAO, "FoodLogDAO не должен быть null");
        this.workoutLogDAO = Objects.requireNonNull(workoutLogDAO, "WorkoutLogDAO не должен быть null");
        this.bmrdao = Objects.requireNonNull(bmrdao, "BMRDAO не должен быть null");
        this.userDAO = Objects.requireNonNull(userDAO, "UserDAO не должен быть null");
        this.weightLogDAO = Objects.requireNonNull(weightLogDAO, "WeightLogDAO не должен быть null");
    }

    public int calculateDailyNeatExpenditure(int userId) {
    int bmr = bmrdao.calculateBMR(userId);
    User user = userDAO.selectUser(userId);
    return (int) (bmr * user.getActivityLevel().getCoefficient());
    }

    public int calculateDailyWorkoutExpenditure(int userId, Date date) {
        return workoutLogDAO.getDailyTotalBurned(userId, date);
    }

    public int calculateDailyTotalExpenditure(int userId, Date date) {
        return calculateDailyNeatExpenditure(userId) + calculateDailyWorkoutExpenditure(userId, date);
    }

    public BigDecimal calculateBMI(int userId) {
        User user = userDAO.selectUser(userId);
        if (user == null) return BigDecimal.ZERO;

        BigDecimal currentWeight = weightLogDAO.getAbsoluteLatestWeight(userId);
        if (currentWeight == null) currentWeight = user.getStartWeightKg();

        int heightCm = user.getHeightCm();
        if (heightCm == 0 || currentWeight == null) return BigDecimal.ZERO;

        BigDecimal heightM = new BigDecimal(heightCm).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal heightSquared = heightM.multiply(heightM);

        if (heightSquared.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return currentWeight.divide(heightSquared, 2, RoundingMode.HALF_UP);
    }

    public String getBMICategory(BigDecimal bmi) {
        if (bmi.compareTo(new BigDecimal("18.5")) < 0) return "Недостаточный вес";
        if (bmi.compareTo(new BigDecimal("25.0")) < 0) return "Нормальный вес";
        if (bmi.compareTo(new BigDecimal("30.0")) < 0) return "Избыточный вес";
        if (bmi.compareTo(new BigDecimal("35.0")) < 0) return "Ожирение I степени";
        if (bmi.compareTo(new BigDecimal("40.0")) < 0) return "Ожирение II степени";
        return "Ожирение III степени";
    }

    public int getDailyConsumedCalories(int userId, Date date) {
        return foodLogDAO.getDailyTotalCalories(userId, date);
    }

    public int getDailyBurnedCalories(int userId, Date date) {
        return workoutLogDAO.getDailyTotalBurned(userId, date);
    }
}