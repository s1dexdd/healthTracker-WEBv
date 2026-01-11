package com.healthtracker.config;

import com.healthtracker.dao.*;
import com.healthtracker.service.GoalCalculationService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDAO userDAO() {
        return new UserDAO();
    }

    @Bean
    public WeightLogDAO weightLogDAO() {
        return new WeightLogDAO();
    }

    @Bean
    public FoodLogDAO foodLogDAO() {
        return new FoodLogDAO();
    }

    @Bean
    public WorkoutLogDAO workoutLogDAO() {
        return new WorkoutLogDAO();
    }

    @Bean
    public BMRDAO bmrDAO(UserDAO userDAO, WeightLogDAO weightLogDAO) {
        return new BMRDAO(userDAO, weightLogDAO);
    }

    @Bean
    public ReportDAO reportDAO(FoodLogDAO f, WorkoutLogDAO wr, BMRDAO b, UserDAO u, WeightLogDAO we) {
        return new ReportDAO(f, wr, b, u, we);
    }
    @Bean
    public GoalCalculationService goalCalculationService(ReportDAO reportDAO, WeightLogDAO weightLogDAO, UserDAO userDAO) {
        return new GoalCalculationService(reportDAO, weightLogDAO, userDAO);
    }
}