package com.healthtracker.model;

public class GoalResult {
    private int targetIntakeKcal;
    private int dailyDeficitOrSurplusKcal;
    private String goalDescription;
    private float totalWeightChangeKg;
    private int proteinGrams;
    private int fatsGrams;
    private int carbsGrams;
    
    private int currentIntakeKcal; 
    private int currentBurnedKcal;
    private int currentProteinGrams;
    private int currentFatsGrams;
    private int currentCarbsGrams;

    public GoalResult() {}

    public GoalResult(int targetIntakeKcal, int dailyDeficitOrSurplusKcal, String goalDescription, 
                      float totalWeightChangeKg, int proteinGrams, int fatsGrams, int carbsGrams) {
        this.targetIntakeKcal = targetIntakeKcal;
        this.dailyDeficitOrSurplusKcal = dailyDeficitOrSurplusKcal;
        this.goalDescription = goalDescription;
        this.totalWeightChangeKg = totalWeightChangeKg;
        this.proteinGrams = proteinGrams;
        this.fatsGrams = fatsGrams;
        this.carbsGrams = carbsGrams;
    }

    public int getTargetIntakeKcal() { return targetIntakeKcal; }
    public int getDailyDeficitOrSurplusKcal() { return dailyDeficitOrSurplusKcal; }
    public String getGoalDescription() { return goalDescription; }
    public float getTotalWeightChangeKg() { return totalWeightChangeKg; }
    public int getProteinGrams() { return proteinGrams; }
    public int getFatsGrams() { return fatsGrams; }
    public int getCarbsGrams() { return carbsGrams; }

    public int getCurrentIntakeKcal() { return currentIntakeKcal; }
    public void setCurrentIntakeKcal(int val) { this.currentIntakeKcal = val; }

    public int getCurrentBurnedKcal() { return currentBurnedKcal; }
    public void setCurrentBurnedKcal(int val) { this.currentBurnedKcal = val; }

    public int getCurrentProteinGrams() { return currentProteinGrams; }
    public void setCurrentProteinGrams(int val) { this.currentProteinGrams = val; }

    public int getCurrentFatsGrams() { return currentFatsGrams; }
    public void setCurrentFatsGrams(int val) { this.currentFatsGrams = val; }

    public int getCurrentCarbsGrams() { return currentCarbsGrams; }
    public void setCurrentCarbsGrams(int val) { this.currentCarbsGrams = val; }
}