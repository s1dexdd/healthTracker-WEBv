import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { GoalResult } from '../models/goal-result.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) { }

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
    return this.http.get<GoalResult>(`${this.apiUrl}/${userId}/calculate-goal?weeklyRate=${weeklyRate}`);
  }

  addFoodLog(log: any): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/food`, log);
  }

  addWorkoutLog(log: any): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/workout`, log);
  }

  getFoodLogs(userId: number, date: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/food-logs?userId=${userId}&date=${date}`);
  }

  getWorkoutLogs(userId: number, date: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workout-logs?userId=${userId}&date=${date}`);
  }

  deleteFoodLog(logId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/food/${logId}`);
  }
}