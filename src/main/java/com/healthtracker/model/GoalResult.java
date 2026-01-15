package com.healthtracker.model;

public class GoalResult {
    private int targetIntakeKcal;
    private int dailyAdjustment;
    private String goalDescription;
    private float weeklyRateKg;
    private int proteinGrams;
    private int fatsGrams;
    private int carbsGrams;
    private int tdee;

    private int currentIntakeKcal;
    private int currentBurnedKcal;
    private int currentProteinGrams;
    private int currentFatsGrams;
    private int currentCarbsGrams;
    private float currentWeightKg;

    public GoalResult(int targetIntakeKcal, int dailyAdjustment, String goalDescription, float weeklyRateKg, int proteinGrams, int fatsGrams, int carbsGrams) {
        this.targetIntakeKcal = targetIntakeKcal;
        this.dailyAdjustment = dailyAdjustment;
        this.goalDescription = goalDescription;
        this.weeklyRateKg = weeklyRateKg;
        this.proteinGrams = proteinGrams;
        this.fatsGrams = fatsGrams;
        this.carbsGrams = carbsGrams;
    }

    public int getTargetIntakeKcal() { return targetIntakeKcal; }
    public int getDailyAdjustment() { return dailyAdjustment; }
    public String getGoalDescription() { return goalDescription; }
    public float getWeeklyRateKg() { return weeklyRateKg; }
    public int getProteinGrams() { return proteinGrams; }
    public int getFatsGrams() { return fatsGrams; }
    public int getCarbsGrams() { return carbsGrams; }

    public int getTdee() { return tdee; }
    public void setTdee(int tdee) { this.tdee = tdee; }

    public int getCurrentIntakeKcal() { return currentIntakeKcal; }
    public void setCurrentIntakeKcal(int currentIntakeKcal) { this.currentIntakeKcal = currentIntakeKcal; }

    public int getCurrentBurnedKcal() { return currentBurnedKcal; }
    public void setCurrentBurnedKcal(int currentBurnedKcal) { this.currentBurnedKcal = currentBurnedKcal; }

    public int getCurrentProteinGrams() { return currentProteinGrams; }
    public void setCurrentProteinGrams(int currentProteinGrams) { this.currentProteinGrams = currentProteinGrams; }

    public int getCurrentFatsGrams() { return currentFatsGrams; }
    public void setCurrentFatsGrams(int currentFatsGrams) { this.currentFatsGrams = currentFatsGrams; }

    public int getCurrentCarbsGrams() { return currentCarbsGrams; }
    public void setCurrentCarbsGrams(int currentCarbsGrams) { this.currentCarbsGrams = currentCarbsGrams; }

    public float getCurrentWeightKg() { return currentWeightKg; }
    public void setCurrentWeightKg(float currentWeightKg) { this.currentWeightKg = currentWeightKg; }
}