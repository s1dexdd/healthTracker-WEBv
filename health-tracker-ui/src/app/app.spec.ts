import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { UserService } from './services/user.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let userService: UserService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        HttpClientTestingModule,
        NoopAnimationsModule,
        ReactiveFormsModule
      ],
      providers: [UserService]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate stats correctly', () => {
    component.goal = {
      targetIntakeKcal: 2000,
      currentIntakeKcal: 1000,
      currentBurnedKcal: 200,
      proteinGrams: 100,
      currentProteinGrams: 50,
      fatsGrams: 50,
      currentFatsGrams: 25,
      carbsGrams: 200,
      currentCarbsGrams: 100
    } as any;

    component.calculateStats();

    expect(component.remainingKcal).toBe(1200);
    expect(component.proteinPercent).toBe(50);
    expect(component.fatsPercent).toBe(50);
    expect(component.carbsPercent).toBe(50);
  });

  it('should login and load dashboard', fakeAsync(() => {
    const mockUser = { userId: 1, name: 'Test' };
    spyOn(userService, 'login').and.returnValue(of(mockUser as any));
    
    spyOn(userService, 'getGoalCalculation').and.returnValue(of({} as any));
    spyOn(userService, 'getWeightLogs').and.returnValue(of([]));
    spyOn(userService, 'getFoodLogs').and.returnValue(of([]));
    spyOn(userService, 'getWorkoutLogs').and.returnValue(of([]));

    const spyLoadDashboard = spyOn(component, 'loadDashboard').and.callThrough();

    component.isLoginMode = true;
    component.authForm.setValue({ email: 'test@test.com', password: '123456' });
    
    component.handleAuth();
    
    tick();
    fixture.detectChanges();

    expect(component.user).toEqual(mockUser as any);
    expect(component.currentStep).toBe('main');
    expect(spyLoadDashboard).toHaveBeenCalled();
  }));

  it('should handle logout', () => {
    component.user = { userId: 1 } as any;
    component.currentStep = 'main';
    
    component.logout();
    
    expect(component.user).toBeNull();
    expect(component.currentStep).toBe('auth');
  });
});