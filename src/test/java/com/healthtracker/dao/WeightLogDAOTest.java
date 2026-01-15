package com.healthtracker.dao;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class WeightLogDAOTest {

    private WeightLogDAO weightLogDAO;
    private UserDAO userDAO;
    private int testUserId;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialise();
        clearDatabase();
        weightLogDAO = new WeightLogDAO();
        userDAO = new UserDAO();

        User user = new User();
        user.setName("Weight Test User");
        user.setEmail("weight@test.com");
        user.setPassword("pass");
        user.setHeightCm(180);
        user.setStartWeightKg(new BigDecimal("80.0"));
        user.setTargetWeightKg(new BigDecimal("75.0"));
        user.setAge(25);
        user.setGender(User.Gender.MALE);
        user.setActivityLevel(User.ActivityLevel.MID);
        testUserId = userDAO.insertUser(user);
    }

    private void clearDatabase() {
        try (java.sql.Connection conn = com.healthtracker.util.DBConfig.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("TRUNCATE TABLE \"USER\"");
            stmt.execute("TRUNCATE TABLE WEIGHT_LOG");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertAndGetLatestWeight() {
        BigDecimal weight = new BigDecimal("85.50");
        WeightLog log = new WeightLog(testUserId, new Date(System.currentTimeMillis()), weight);

        weightLogDAO.insertWeightLog(log);
        BigDecimal latest = weightLogDAO.getAbsoluteLatestWeight(testUserId);

        assertThat(latest).isNotNull();
        assertThat(latest).isEqualByComparingTo(weight);
    }

    @Test
    void testGetWeightLogsByUserId() {
        weightLogDAO.insertWeightLog(new WeightLog(testUserId, Date.valueOf("2025-01-01"), new BigDecimal("80.0")));
        weightLogDAO.insertWeightLog(new WeightLog(testUserId, Date.valueOf("2025-01-02"), new BigDecimal("79.5")));

        List<WeightLog> logs = weightLogDAO.getWeightLogsByUserId(testUserId);

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getCurrentWeightKg()).isEqualByComparingTo("79.5");
    }
}