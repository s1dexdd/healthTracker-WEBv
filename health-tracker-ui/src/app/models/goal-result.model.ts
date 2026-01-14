export interface GoalResult {
  targetIntakeKcal: number;
  proteinGrams: number;
  fatsGrams: number;
  carbsGrams: number;
  goalDescription: string;
  currentIntakeKcal?: number;
  currentBurnedKcal?: number;
  currentProteinGrams?: number;
  currentFatsGrams?: number;
  currentCarbsGrams?: number;
  currentWeightKg?: number; 
}