package com.healthtracker.dao;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialise();
        clearDatabase();
        userDAO = new UserDAO();
    }

    private void clearDatabase() {
        try (java.sql.Connection conn = com.healthtracker.util.DBConfig.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("TRUNCATE TABLE \"USER\"");
            stmt.execute("TRUNCATE TABLE WEIGHT_LOG");
            stmt.execute("TRUNCATE TABLE FOOD_LOG");
            stmt.execute("TRUNCATE TABLE WORKOUT_LOG");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertUserAndSelectById() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setHeightCm(180);
        user.setStartWeightKg(new BigDecimal("85.00"));
        user.setTargetWeightKg(new BigDecimal("80.00"));
        user.setAge(30);
        user.setGender(User.Gender.MALE);
        user.setActivityLevel(User.ActivityLevel.MID);
        user.setWeeklyGoalKg(new BigDecimal("0.5"));

        int userId = userDAO.insertUser(user);
        assertThat(userId).isGreaterThan(0);

        User selectedUser = userDAO.selectUser(userId);
        assertThat(selectedUser).isNotNull();
        assertThat(selectedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testGetUserByEmail() {
        User user = new User();
        user.setName("Email Test");
        user.setEmail("unique@example.com");
        user.setPassword("secret");
        user.setHeightCm(170);
        user.setStartWeightKg(new BigDecimal("70.0"));
        user.setTargetWeightKg(new BigDecimal("65.0"));
        user.setAge(25);
        user.setGender(User.Gender.FEMALE);
        user.setActivityLevel(User.ActivityLevel.LIGHT);
        
        userDAO.insertUser(user);

        User foundUser = userDAO.getUserByEmail("unique@example.com");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Email Test");
    }

    @Test
    void testUpdateUserSettings() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("update@example.com");
        user.setHeightCm(175);
        user.setStartWeightKg(new BigDecimal("90.0"));
        user.setTargetWeightKg(new BigDecimal("85.0"));
        user.setAge(40);
        user.setGender(User.Gender.MALE);
        user.setActivityLevel(User.ActivityLevel.SIT);
        
        int userId = userDAO.insertUser(user);

        boolean updated = userDAO.updateUserAllSettings(
                userId, 
                "New Name", 
                180, 
                new BigDecimal("88.0"), 
                new BigDecimal("80.0"), 
                41, 
                User.Gender.MALE, 
                User.ActivityLevel.MID, 
                new BigDecimal("1.0")
        );

        assertThat(updated).isTrue();

        User updatedUser = userDAO.selectUser(userId);
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }

    @Test
    void testGetUserByEmailNotFound() {
        User foundUser = userDAO.getUserByEmail("nonexistent@example.com");
        assertThat(foundUser).isNull();
    }
    @Test
void testDeleteUser_CascadesToLogs() throws java.sql.SQLException {
    User user = new User();
    user.setName("Delete Me");
    user.setEmail("delete@test.com");
    user.setStartWeightKg(new BigDecimal("70"));
    user.setTargetWeightKg(new BigDecimal("65"));
    user.setHeightCm(170);
    user.setAge(30);
    user.setGender(User.Gender.MALE);
    user.setActivityLevel(User.ActivityLevel.SIT);
    int userId = userDAO.insertUser(user);

    WeightLog weightLog = new WeightLog(userId, new java.sql.Date(System.currentTimeMillis()), new BigDecimal("70"));
    new WeightLogDAO().insertWeightLog(weightLog);

    try (java.sql.Connection conn = com.healthtracker.util.DBConfig.getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM \"USER\" WHERE user_id = ?")) {
        ps.setInt(1, userId);
        ps.executeUpdate();
    }

    List<WeightLog> logs = new WeightLogDAO().getWeightLogsByUserId(userId);
    assertThat(logs).isEmpty();
}
}