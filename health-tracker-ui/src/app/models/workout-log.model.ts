export interface WorkoutLog {
  workoutId?: number;
  userId: number;
  logDate: string;
  type: string;
  durationMinutes: number;
  caloriesBurnedPerMinute: number;
  caloriesBurned?: number;
}