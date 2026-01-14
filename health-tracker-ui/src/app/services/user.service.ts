import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { GoalResult } from '../models/goal-result.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8081/api/users';

  login(credentials: any): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/login`, credentials);
  }

  register(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, user);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  saveUser(user: User): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/update-profile`, user);
  }

  getGoalCalculation(userId: number, weeklyRate: number): Observable<GoalResult> {
    return this.http.get<GoalResult>(`${this.apiUrl}/${userId}/calculate-goal`, {
      params: { weeklyRate: weeklyRate.toString() }
    });
  }

  addFoodLog(log: any): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/food`, log);
  }

  deleteFoodLog(logId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/food/${logId}`);
  }

  addWorkoutLog(log: any): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/workout`, log);
  }

  deleteWorkoutLog(workoutId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/workout/${workoutId}`);
  }

  addWeightLog(log: { userId: number; currentWeightKg: number; logDate: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/weight`, log);
  }

  getWeightLogs(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/weight-logs`, {
      params: { userId: userId.toString() }
    });
  }

  getFoodLogs(userId: number, date: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/food-logs`, {
      params: { userId: userId.toString(), date: date }
    });
  }

  getWorkoutLogs(userId: number, date: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workout-logs`, {
      params: { userId: userId.toString(), date: date }
    });
  }
}