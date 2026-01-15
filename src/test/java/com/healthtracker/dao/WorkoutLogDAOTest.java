package com.healthtracker.dao;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.WorkoutLog;
import com.healthtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class WorkoutLogDAOTest {

    private WorkoutLogDAO workoutLogDAO;
    private UserDAO userDAO;
    private int testUserId;

    @BeforeEach
void setUp() {
    DatabaseInitializer.initialise();
    workoutLogDAO = new WorkoutLogDAO();
    userDAO = new UserDAO();

    
    User user = new User();
    user.setName("Test Runner");
    user.setEmail("test" + System.currentTimeMillis() + "@test.com"); 
    user.setPassword("123");
    user.setHeightCm(180);
    user.setAge(25);
    user.setGender(User.Gender.MALE);
    user.setActivityLevel(User.ActivityLevel.MID);
    user.setStartWeightKg(new BigDecimal("90.00"));
    user.setTargetWeightKg(new BigDecimal("80.00"));
    user.setWeeklyGoalKg(new BigDecimal("0.50"));

    
    testUserId = userDAO.insertUser(user);
    
    if (testUserId == -1) {
        throw new RuntimeException("Не удалось создать тестового пользователя. Проверьте логи БД.");
    }
}

    @Test
    void testInsertAndSumCalories() {
        Date date = new Date(System.currentTimeMillis());
        
        WorkoutLog w1 = new WorkoutLog(testUserId, "Run", 30, 10);
        WorkoutLog w2 = new WorkoutLog(testUserId, "Walk", 60, 4);
        
        workoutLogDAO.insertWorkoutLog(w1);
        workoutLogDAO.insertWorkoutLog(w2);

        int totalBurned = workoutLogDAO.getDailyTotalBurned(testUserId, date);

        assertThat(totalBurned).isEqualTo(300 + 240);
    }
}