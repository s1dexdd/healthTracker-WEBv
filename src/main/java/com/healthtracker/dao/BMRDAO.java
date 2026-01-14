package com.healthtracker.dao;

import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import java.math.BigDecimal;

public class BMRDAO {
    private final UserDAO userDAO;
    private final WeightLogDAO weightLogDAO;

    public BMRDAO(UserDAO userDAO, WeightLogDAO weightLogDAO) {
        this.userDAO = userDAO;
        this.weightLogDAO = weightLogDAO;
    }

    public int calculateBMR(int userId) {
        User user = userDAO.selectUser(userId);
        if (user == null) return 0;

        BigDecimal currentWeight = weightLogDAO.getAbsoluteLatestWeight(userId);
        double weight = (currentWeight != null) ? currentWeight.doubleValue() : user.getStartWeightKg().doubleValue();
        double height = user.getHeightCm();
        int age = user.getAge();

        double bmr;
        if (user.getGender() == User.Gender.MALE) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        return (int) Math.round(bmr);
    }
}