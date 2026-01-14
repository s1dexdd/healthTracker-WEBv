package com.healthtracker.dao;

import com.healthtracker.model.User;
import com.healthtracker.util.DBConfig;
import java.sql.*;
import java.math.BigDecimal;

public class UserDAO {

    private static final String INSERT_USERS_SQL =
            "INSERT INTO \"USER\" (name, email, password, height_cm, start_weight_kg, target_weight_kg, age, gender, activity_level, weekly_goal_kg) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_USER_BY_ID =
            "SELECT * FROM \"USER\" WHERE user_id = ?";

    private static final String SELECT_USER_BY_EMAIL =
            "SELECT * FROM \"USER\" WHERE email = ?";

    private static final String UPDATE_USER_ALL_SETTINGS_SQL =
            "UPDATE \"USER\" SET name = ?, height_cm = ?, start_weight_kg = ?, target_weight_kg = ?, age = ?, gender = ?, activity_level = ?, weekly_goal_kg = ? WHERE user_id = ?";

    public int insertUser(User user) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_USERS_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getHeightCm());
            ps.setBigDecimal(5, user.getStartWeightKg());
            ps.setBigDecimal(6, user.getTargetWeightKg());
            ps.setInt(7, user.getAge());
            ps.setString(8, user.getGender().name());
            ps.setString(9, user.getActivityLevel().name());
            ps.setBigDecimal(10, user.getWeeklyGoalKg());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User getUserByEmail(String email) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User selectUser(int userId) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserAllSettings(int userId, String name, int heightCm, BigDecimal startWeightKg, BigDecimal targetWeightKg, int age, User.Gender gender, User.ActivityLevel activityLevel, BigDecimal weeklyGoal) {
    try (Connection connection = DBConfig.getConnection();
         PreparedStatement ps = connection.prepareStatement(UPDATE_USER_ALL_SETTINGS_SQL)) {
        ps.setString(1, name);
        ps.setInt(2, heightCm);
        ps.setBigDecimal(3, startWeightKg);
        ps.setBigDecimal(4, targetWeightKg);
        ps.setInt(5, age);
        ps.setString(6, gender.name());
        ps.setString(7, activityLevel.name());
        ps.setBigDecimal(8, weeklyGoal);
        ps.setInt(9, userId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setHeightCm(rs.getInt("height_cm"));
        user.setStartWeightKg(rs.getBigDecimal("start_weight_kg"));
        user.setTargetWeightKg(rs.getBigDecimal("target_weight_kg"));
        user.setAge(rs.getInt("age"));
        user.setGender(User.Gender.valueOf(rs.getString("gender")));
        user.setActivityLevel(User.ActivityLevel.valueOf(rs.getString("activity_level")));
        user.setWeeklyGoalKg(rs.getBigDecimal("weekly_goal_kg"));
        return user;
    }
}