export interface FoodLog {
  foodId?: number;
  userId: number;
  logDate: string;
  description: string;
  mealType: string;
  caloriesPer100g: number;
  portionSizeGrams: number;
  proteinPer100g: number;
  fatsPer100g: number;
  carbsPer100g: number;
  calories?: number;
  proteinG?: number; 
  fatsG?: number;    
  carbsG?: number;
}