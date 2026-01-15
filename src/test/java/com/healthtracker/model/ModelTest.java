package com.healthtracker.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import static org.assertj.core.api.Assertions.assertThat;

public class ModelTest {

    @Test
    void testWorkoutLogCalculation() {
        WorkoutLog log = new WorkoutLog();
        log.setDurationMinutes(30);
        log.setCaloriesBurnedPerMinute(10);
        assertThat(log.calculateTotalBurned()).isEqualTo(300);
    }

    @Test
    void testFoodLogCalculations() {
        FoodLog food = new FoodLog();
        food.setPortionSizeGrams(200);
        food.setCaloriesPer100g(52);
        food.setProteinPer100g(new BigDecimal("0.3"));
        food.setFatsPer100g(new BigDecimal("0.2"));
        food.setCarbsPer100g(new BigDecimal("14.0"));

        assertThat(food.calculateTotalCalories()).isEqualTo(104);
        assertThat(food.calculateTotalProtein()).isEqualByComparingTo("0.6");
        assertThat(food.calculateTotalFats()).isEqualByComparingTo("0.4");
        assertThat(food.calculateTotalCarbs()).isEqualByComparingTo("28.0");
    }

    @Test
    void testGoalResultProperties() {
        GoalResult result = new GoalResult(2000, 500, "Похудение", 0.5f, 150, 70, 200);
        assertThat(result.getTargetIntakeKcal()).isEqualTo(2000);
        assertThat(result.getDailyAdjustment()).isEqualTo(500);
    }

    @Test
    void testWeightLogConstructor() {
        Date date = Date.valueOf("2025-01-01");
        BigDecimal weight = new BigDecimal("75.5");
        WeightLog log = new WeightLog(1, 10, date, weight);

        assertThat(log.getUserId()).isEqualTo(10);
        assertThat(log.getLogDate()).isEqualTo(date);
        assertThat(log.getCurrentWeightKg()).isEqualByComparingTo("75.5");
    }

    @Test
    void testActivityLevelCoefficients() {
        assertThat(User.ActivityLevel.SIT.getCoefficient()).isEqualTo(1.2f);
        assertThat(User.ActivityLevel.LIGHT.getCoefficient()).isEqualTo(1.375f);
        assertThat(User.ActivityLevel.MID.getCoefficient()).isEqualTo(1.55f);
    }
}