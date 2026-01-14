package com.healthtracker.model;

import java.math.BigDecimal;
import java.io.Serializable;

public class User implements Serializable {

    private int userId;
    private String name;
    private String email;
    private String password;
    private int heightCm;
    private BigDecimal startWeightKg;
    private BigDecimal targetWeightKg;
    private int age;
    private BigDecimal weeklyGoalKg;

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum ActivityLevel {
        SIT(1.2f),
        LIGHT(1.375f),
        MID(1.55f);

        private final float coefficient;

        ActivityLevel(float coefficient) {
            this.coefficient = coefficient;
        }

        public float getCoefficient() {
            return coefficient;
        }
    }

    private Gender gender;
    private ActivityLevel activityLevel;

    public User() {
    }

    public User(int userId, String name, String email, String password, int heightCm, int age, Gender gender, BigDecimal startWeightKg, BigDecimal targetWeightKg, ActivityLevel activityLevel, BigDecimal weeklyGoalKg) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.heightCm = heightCm;
        this.age = age;
        this.gender = gender;
        this.startWeightKg = startWeightKg;
        this.targetWeightKg = targetWeightKg;
        this.activityLevel = activityLevel;
        this.weeklyGoalKg = weeklyGoalKg;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getStartWeightKg() {
        return startWeightKg;
    }

    public void setStartWeightKg(BigDecimal startWeightKg) {
        this.startWeightKg = startWeightKg;
    }

    public BigDecimal getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(BigDecimal targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public BigDecimal getWeeklyGoalKg() {
        return weeklyGoalKg;
    }

    public void setWeeklyGoalKg(BigDecimal weeklyGoalKg) {
        this.weeklyGoalKg = weeklyGoalKg;
    }

    @Override
    public String toString() {
        return "User [ID=" + userId + ", Name='" + name + "', Email='" + email + "', Height=" + heightCm + ", Age=" + age + ", Gender=" + gender +
                ", StartWeight=" + startWeightKg + ", TargetWeight=" + targetWeightKg + ", Activity=" + activityLevel + 
                ", WeeklyGoal=" + weeklyGoalKg + "]";
    }
}