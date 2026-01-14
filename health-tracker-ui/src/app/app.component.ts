import { Component, OnInit, inject, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { UserService } from './services/user.service';
import { User } from './models/user.model';
import { GoalResult } from './models/goal-result.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatSelectModule, MatTabsModule, MatIconModule, MatProgressBarModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  currentStep: 'auth' | 'setup' | 'main' = 'auth';
  isLoginMode = true;
  activeTab = 0;
  errorMessage: string | null = null;
  selectedDate = new Date().toLocaleDateString('en-CA');

  user: User | null = null;
  goal: GoalResult | null = null;
  dailyFoodLogs: any[] = [];
  dailyWorkoutLogs: any[] = [];
  availableGoals: { label: string, value: number }[] = [];

  newWeightLog: number | null = null;
  lastEnteredWeight: number | null = null;

  proteinPercent = 0;
  fatsPercent = 0;
  carbsPercent = 0;
  remainingKcal = 0;

  authForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  setupForm = this.fb.group({
    name: ['', Validators.required],
    heightCm: [170, Validators.required],
    startWeightKg: [70, Validators.required],
    targetWeightKg: [65, Validators.required],
    age: [25, Validators.required],
    gender: ['MALE'],
    activityLevel: ['SIT'],
    weeklyGoalKg: [0]
  });

  foodForm = this.fb.group({
    description: ['', Validators.required],
    caloriesPer100g: [0, Validators.required],
    portionSizeGrams: [0, Validators.required],
    proteinPer100g: [0],
    fatsPer100g: [0],
    carbsPer100g: [0]
  });

  workoutForm = this.fb.group({
    type: ['', Validators.required],
    durationMinutes: [0, Validators.required],
    caloriesBurnedPerMinute: [0, Validators.required]
  });

  ngOnInit() {
    this.setupForm.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged((prev, curr) => 
        prev.startWeightKg === curr.startWeightKg && prev.targetWeightKg === curr.targetWeightKg
      )
    ).subscribe(() => this.updateAvailableGoals());
  }

  updateAvailableGoals() {
    const cur = this.setupForm.value.startWeightKg || 0;
    const tar = this.setupForm.value.targetWeightKg || 0;
    const goals = [{ label: 'Поддержание', value: 0 }];

    if (tar < cur) {
      goals.push(
        { label: 'Мягкое похудение (-0.5 кг/нед)', value: 0.5 },
        { label: 'Интенсивное (-1.0 кг/нед)', value: 1.0 }
      );
    } else if (tar > cur) {
      goals.push(
        { label: 'Набор массы (+0.5 кг/нед)', value: -0.5 }
      );
    }

    this.availableGoals = goals;
    const currentGoal = this.setupForm.value.weeklyGoalKg;
    if (!this.availableGoals.find(g => g.value === currentGoal)) {
      this.setupForm.patchValue({ weeklyGoalKg: 0 }, { emitEvent: false });
    }
  }

  handleAuth() {
    if (this.authForm.invalid) return;
    const creds = this.authForm.value;
    if (this.isLoginMode) {
      this.userService.login(creds).subscribe({
        next: (res) => {
          this.user = res;
          this.setupForm.patchValue(res as any);
          this.currentStep = 'main';
          this.updateAvailableGoals();
          this.loadDashboard();
        },
        error: () => this.errorMessage = 'Ошибка входа'
      });
    } else {
      const newUser = { ...this.setupForm.value, ...creds } as User;
      this.userService.register(newUser).subscribe({
        next: (res) => { 
          this.user = res; 
          this.currentStep = 'setup'; 
          this.updateAvailableGoals(); 
        },
        error: () => this.errorMessage = 'Ошибка регистрации'
      });
    }
  }

  saveProfileData() {
    if (!this.user?.userId) return;
    const updated = { ...this.user, ...this.setupForm.value } as User;
    this.userService.saveUser(updated).subscribe(() => {
      this.user = updated;
      this.loadDashboard();
    });
  }

  updateWeight() {
    if (!this.newWeightLog || this.newWeightLog <= 0 || !this.user?.userId) return;

    const weightValue = this.newWeightLog;
    this.lastEnteredWeight = weightValue;

    const weightData = {
      userId: this.user.userId,
      currentWeightKg: weightValue,
      logDate: this.selectedDate
    };

    this.userService.addWeightLog(weightData).subscribe({
      next: () => {
        this.newWeightLog = null;
        this.loadDashboard();
      },
      error: (err: any) => console.error('Ошибка сохранения веса', err)
    });
  }

  loadDashboard() {
    if (!this.user?.userId) return;
    const userId = this.user.userId; // Сохраняем в константу для уверенности TS
    const rate = this.setupForm.value.weeklyGoalKg || 0;
    
    this.userService.getGoalCalculation(userId, rate).subscribe(res => {
      this.goal = res;
      
      // Используем userId без знака вопроса, так как мы выше проверили его наличие
      this.userService.getWeightLogs(userId).subscribe((logs: any[]) => {
        if (logs && logs.length > 0) {
          const sortedLogs = logs.sort((a: any, b: any) => 
            new Date(b.logDate).getTime() - new Date(a.logDate).getTime()
          );
          const latestWeight = sortedLogs[0].currentWeightKg;
          
          if (this.user) {
            this.user.startWeightKg = latestWeight;
            this.setupForm.patchValue({ startWeightKg: latestWeight }, { emitEvent: false });
          }
          if (this.goal) {
            this.goal.currentWeightKg = latestWeight;
          }
        } else if (this.lastEnteredWeight) {
          if (this.goal) this.goal.currentWeightKg = this.lastEnteredWeight;
        } else {
          if (this.goal) this.goal.currentWeightKg = this.user?.startWeightKg;
        }
        this.calculateStats();
      });
    });

    this.userService.getFoodLogs(userId, this.selectedDate).subscribe(logs => {
      this.dailyFoodLogs = logs;
    });

    this.userService.getWorkoutLogs(userId, this.selectedDate).subscribe(logs => {
      this.dailyWorkoutLogs = logs;
    });
  }
  calculateStats() {
    if (!this.goal) return;
    this.remainingKcal = (this.goal.targetIntakeKcal || 0) - (this.goal.currentIntakeKcal || 0) + (this.goal.currentBurnedKcal || 0);
    this.proteinPercent = this.calcPct(this.goal.currentProteinGrams, this.goal.proteinGrams);
    this.fatsPercent = this.calcPct(this.goal.currentFatsGrams, this.goal.fatsGrams);
    this.carbsPercent = this.calcPct(this.goal.currentCarbsGrams, this.goal.carbsGrams);
  }

  private calcPct(c = 0, t = 0) { return t > 0 ? Math.min((c / t) * 100, 100) : 0; }

  addFood() {
    if (this.foodForm.invalid || !this.user?.userId) return;
    const foodData = {
      ...this.foodForm.value,
      userId: this.user.userId,
      logDate: this.selectedDate,
      mealType: 'ANY'
    };
    this.userService.addFoodLog(foodData).subscribe(() => { 
      this.loadDashboard(); 
      this.foodForm.reset({ caloriesPer100g: 0, portionSizeGrams: 0, proteinPer100g: 0, fatsPer100g: 0, carbsPer100g: 0 }); 
    });
  }

  deleteFoodLog(id?: number) { 
    if (id) this.userService.deleteFoodLog(id).subscribe(() => this.loadDashboard()); 
  }

  addWorkout() {
    if (this.workoutForm.invalid || !this.user?.userId) return;
    this.userService.addWorkoutLog({ ...this.workoutForm.value, userId: this.user.userId, logDate: this.selectedDate })
      .subscribe(() => { 
        this.loadDashboard(); 
        this.workoutForm.reset({ durationMinutes: 0, caloriesBurnedPerMinute: 0 }); 
      });
  }

  deleteWorkoutLog(id?: number) { 
    if (id) this.userService.deleteWorkoutLog(id).subscribe(() => this.loadDashboard()); 
  }

  logout() { 
    this.currentStep = 'auth'; 
    this.user = null; 
    this.lastEnteredWeight = null;
    this.authForm.reset(); 
  }
}