export interface User {
  userId?: number;
  name: string;
  email?: string;
  password?: string;
  heightCm: number;
  startWeightKg: number;
  targetWeightKg: number;
  age: number;
  gender: 'MALE' | 'FEMALE';
  activityLevel: 'SIT' | 'LIGHT' | 'MID';
  weeklyGoalKg: number;
}