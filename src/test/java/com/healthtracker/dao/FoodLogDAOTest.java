package com.healthtracker.dao;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.FoodLog;
import com.healthtracker.model.User;
import com.healthtracker.util.DBConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FoodLogDAOTest {

    private FoodLogDAO foodLogDAO;
    private UserDAO userDAO;
    private int testUserId;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialise();
        foodLogDAO = new FoodLogDAO();
        userDAO = new UserDAO();

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM \"FOOD_LOG\"");
            stmt.execute("DELETE FROM \"USER\"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setName("Food Test User");
        user.setEmail("food@test.com");
        user.setPassword("pass123");
        user.setHeightCm(175);
        user.setAge(28);
        user.setGender(User.Gender.MALE);
        user.setStartWeightKg(new BigDecimal("85.00"));
        user.setTargetWeightKg(new BigDecimal("75.00"));
        user.setActivityLevel(User.ActivityLevel.MID);
        user.setWeeklyGoalKg(new BigDecimal("0.5"));

        testUserId = userDAO.insertUser(user);
    }

    @Test
    void testInsertAndGetFoodLogs() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date today = new Date(System.currentTimeMillis());

        FoodLog log = new FoodLog();
        log.setUserId(testUserId);
        log.setLogDate(now);
        log.setDescription("Куриная грудка");
        log.setMealType("LUNCH");
        log.setCaloriesPer100g(165);
        log.setProteinPer100g(new BigDecimal("31.0"));
        log.setFatsPer100g(new BigDecimal("3.6"));
        log.setCarbsPer100g(new BigDecimal("0.0"));
        log.setPortionSizeGrams(200); 
        
        log.setCalories(330);
        log.setProteinG(new BigDecimal("62.0"));
        log.setFatsG(new BigDecimal("7.2"));
        log.setCarbsG(new BigDecimal("0.0"));

        foodLogDAO.insertFoodLog(log);

        List<FoodLog> logs = foodLogDAO.getFoodLogsByDate(testUserId, today);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getDescription()).isEqualTo("Куриная грудка");
        assertThat(logs.get(0).getCalories()).isEqualTo(330);
    }

    @Test
    void testDeleteFoodLog() {
        FoodLog log = new FoodLog();
        log.setUserId(testUserId);
        log.setLogDate(new Timestamp(System.currentTimeMillis()));
        log.setDescription("To Delete");
        log.setMealType("SNACK");
        log.setCaloriesPer100g(100);
        log.setProteinPer100g(BigDecimal.ZERO);
        log.setFatsPer100g(BigDecimal.ZERO);
        log.setCarbsPer100g(BigDecimal.ZERO);
        log.setPortionSizeGrams(100);
        log.setCalories(100);
        log.setProteinG(BigDecimal.ZERO);
        log.setFatsG(BigDecimal.ZERO);
        log.setCarbsG(BigDecimal.ZERO);

        foodLogDAO.insertFoodLog(log);
        
        Date today = new Date(System.currentTimeMillis());
        List<FoodLog> logs = foodLogDAO.getFoodLogsByDate(testUserId, today);
        int foodId = logs.get(0).getFoodId();

        boolean deleted = foodLogDAO.deleteFoodLog(foodId);
        assertThat(deleted).isTrue();

        List<FoodLog> logsAfter = foodLogDAO.getFoodLogsByDate(testUserId, today);
        assertThat(logsAfter).isEmpty();
    }
}