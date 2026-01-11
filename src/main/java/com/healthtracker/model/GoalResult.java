package com.healthtracker.model;

public class GoalResult {
    private int targetIntakeKcal;
    private int dailyDeficitOrSurplusKcal;
    private String goalDescription;
    private float totalWeightChangeKg;
    private int proteinGrams;
    private int fatsGrams;
    private int carbsGrams;
    // Новые поля, которые мы добавили ранее в Angular
    private int currentIntakeKcal; 
    private int currentBurnedKcal;

    // ОБЯЗАТЕЛЬНО: Пустой конструктор для Jackson
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

    // Геттеры для ВСЕХ полей (обязательно для JSON)
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
}