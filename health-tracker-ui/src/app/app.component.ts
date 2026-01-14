import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from './services/user.service';
import { User } from './models/user.model';
import { GoalResult } from './models/goal-result.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  currentStep: 'auth' | 'setup' | 'main' = 'auth';
  isLoginMode: boolean = true;
  activeTab: string = 'diary';
  errorMessage: string | null = null;
  selectedDate: string = new Date().toISOString().split('T')[0];
  dailyFoodLogs: any[] = [];
  dailyWorkoutLogs: any[] = [];
  availableGoals: { label: string, value: number }[] = [];

  kcalPercent: number = 0;
  proteinPercent: number = 0;
  fatsPercent: number = 0;
  carbsPercent: number = 0;
  remainingKcal: number = 0;

  user: User = {
    name: '', email: '', password: '', heightCm: 170,
    startWeightKg: 70, targetWeightKg: 65, age: 25,
    gender: 'MALE', activityLevel: 'SIT', weeklyGoalKg: -0.5
  };

  goal: GoalResult | null = null;

  foodInput = {
    description: '', caloriesPer100g: 0, portionSizeGrams: 0,
    proteinPer100g: 0, fatsPer100g: 0, carbsPer100g: 0
  };

  workoutInput = {
    type: '', durationMinutes: 0, caloriesBurnedPerMinute: 0
  };

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.updateAvailableGoals();
  }

  updateAvailableGoals() {
    const goals = [{ label: 'Поддержание', value: 0 }];
    const current = this.user.startWeightKg || 0;
    const target = this.user.targetWeightKg || 0;

    if (target < current) {
      goals.push(
        { label: '-0.5 кг (Безопасно)', value: -0.5 },
        { label: '-1.0 кг (Экстрим)', value: -1.0 }
      );
    } else if (target > current) {
      goals.push({ label: '+0.5 кг (Набор)', value: 0.5 });
    }
    this.availableGoals = goals;
  }

  updatePercentages() {
    if (!this.goal) {
      this.remainingKcal = 0;
      this.kcalPercent = 0;
      return;
    }

    const target = this.goal.targetIntakeKcal || 0;
    const current = this.goal.currentIntakeKcal || 0;
    const burned = this.goal.currentBurnedKcal || 0;

    this.remainingKcal = target - current + burned;

    const totalAllowed = target + burned;
    this.kcalPercent = totalAllowed > 0 
      ? Math.min((current / totalAllowed) * 100, 100) 
      : 0;

    this.proteinPercent = this.calcMacro(this.goal.currentProteinGrams, this.goal.proteinGrams);
    this.fatsPercent = this.calcMacro(this.goal.currentFatsGrams, this.goal.fatsGrams);
    this.carbsPercent = this.calcMacro(this.goal.currentCarbsGrams, this.goal.carbsGrams);
  }

  private calcMacro(curr: number | undefined, target: number | undefined): number {
    if (!target || target <= 0) return 0;
    return Math.min(((curr || 0) / target) * 100, 100);
  }

  handleAuth() {
    this.errorMessage = null;
    if (this.isLoginMode) {
      this.userService.login({ email: this.user.email, password: this.user.password }).subscribe({
        next: (res: User) => {
          this.user = res;
          this.updateAvailableGoals();
          this.currentStep = 'main';
          this.loadDashboard();
        },
        error: () => this.errorMessage = 'Неверный email или пароль'
      });
    } else {
      this.userService.register(this.user).subscribe({
        next: (res: User) => {
          this.user = res;
          this.updateAvailableGoals();
          this.currentStep = 'setup';
        },
        error: (err) => {
          this.errorMessage = err.status === 409 ? 'Пользователь с таким Email уже существует' : 'Ошибка сервера';
        }
      });
    }
  }

  onDateChange() {
    this.loadDashboard();
  }

  loadDashboard() {
    if (!this.user.userId) return;
    this.userService.getGoalCalculation(this.user.userId, this.user.weeklyGoalKg).subscribe(res => {
      this.goal = res;
      this.updatePercentages();
    });
    this.userService.getFoodLogs(this.user.userId, this.selectedDate).subscribe(logs => {
      this.dailyFoodLogs = logs;
    });
    this.userService.getWorkoutLogs(this.user.userId, this.selectedDate).subscribe(logs => {
      this.dailyWorkoutLogs = logs;
    });
  }

  saveProfileData() {
    if (!this.user.userId) return;
    this.userService.saveUser(this.user).subscribe({
      next: () => {
        this.updateAvailableGoals();
        if (this.currentStep === 'setup') {
          this.currentStep = 'main';
        } else {
          alert('Данные успешно обновлены!');
        }
        this.loadDashboard();
      },
      error: () => alert('Ошибка при сохранении данных профиля')
    });
  }

  addFood() {
    if (!this.user.userId) return;
    const log = {
      ...this.foodInput,
      userId: this.user.userId,
      logDate: this.selectedDate,
      mealType: 'ANY'
    };
    this.userService.addFoodLog(log).subscribe(() => {
      this.loadDashboard();
      this.resetFoodInput();
    });
  }

  resetFoodInput() {
    this.foodInput = {
      description: '', caloriesPer100g: 0, portionSizeGrams: 0,
      proteinPer100g: 0, fatsPer100g: 0, carbsPer100g: 0
    };
  }

  deleteFoodLog(foodId: number) {
    if (!foodId) return;
    if (confirm('Удалить эту запись?')) {
      this.userService.deleteFoodLog(foodId).subscribe(() => {
        this.loadDashboard();
      });
    }
  }

  addWorkout() {
    if (!this.user.userId) return;
    const log = {
      ...this.workoutInput,
      userId: this.user.userId,
      logDate: this.selectedDate
    };
    this.userService.addWorkoutLog(log).subscribe(() => {
      this.loadDashboard();
      this.workoutInput = { type: '', durationMinutes: 0, caloriesBurnedPerMinute: 0 };
    });
  }

  logout() {
    this.currentStep = 'auth';
    this.user = { 
      name: '', email: '', password: '', heightCm: 170, 
      startWeightKg: 70, targetWeightKg: 65, age: 25, 
      gender: 'MALE', activityLevel: 'SIT', weeklyGoalKg: -0.5 
    };
    this.dailyFoodLogs = [];
    this.dailyWorkoutLogs = [];
    this.goal = null;
    this.remainingKcal = 0;
    this.updateAvailableGoals();
  }
}