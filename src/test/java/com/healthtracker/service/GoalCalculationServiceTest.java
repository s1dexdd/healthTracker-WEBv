package com.healthtracker.service;

import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.dao.ReportDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.FoodLog;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalCalculationServiceTest {

    @Mock private ReportDAO reportDAO;
    @Mock private WeightLogDAO weightLogDAO;
    @Mock private UserDAO userDAO;
    @Mock private FoodLogDAO foodLogDAO;

    @InjectMocks
    private GoalCalculationService goalService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setGender(User.Gender.MALE);
        testUser.setStartWeightKg(new BigDecimal("100.0"));
        testUser.setTargetWeightKg(new BigDecimal("90.0"));
    }

    @Test
    void calculateTargetIntake_WeightLoss_CalculatesCorrectly() {
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(2500);
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        BigDecimal weeklyRate = new BigDecimal("0.5");
        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, weeklyRate);

        int expectedAdjustment = 550; 
        
        assertThat(result.getTargetIntakeKcal()).isEqualTo(1950);
        assertThat(result.getDailyAdjustment()).isEqualTo(expectedAdjustment);
        assertThat(result.getGoalDescription()).isEqualTo("Похудение");
    }

    @Test
    void calculateTargetIntake_WeightGain_CalculatesCorrectly() {
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(2500);
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        BigDecimal weeklyRate = new BigDecimal("-0.5");
        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, weeklyRate);

        int expectedAdjustment = -550; 

        assertThat(result.getTargetIntakeKcal()).isEqualTo(3050); 
        assertThat(result.getDailyAdjustment()).isEqualTo(expectedAdjustment);
        assertThat(result.getGoalDescription()).isEqualTo("Набор веса");
    }

    @Test
    void calculateTargetIntake_Maintenance_CalculatesCorrectly() {
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(2500);
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, BigDecimal.ZERO);

        assertThat(result.getTargetIntakeKcal()).isEqualTo(2500);
        assertThat(result.getDailyAdjustment()).isEqualTo(0);
        assertThat(result.getGoalDescription()).isEqualTo("Поддержание веса");
    }

    @Test
    void calculateTargetIntake_EnforcesMinCalories_Female() {
        testUser.setGender(User.Gender.FEMALE);
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(1500); 
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        BigDecimal weeklyRate = new BigDecimal("1.0"); 
        
        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, weeklyRate);

        assertThat(result.getTargetIntakeKcal()).isEqualTo(1200);
    }

    @Test
    void calculateTargetIntake_EnforcesMinCalories_Male() {
        testUser.setGender(User.Gender.MALE);
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(1800); 
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        BigDecimal weeklyRate = new BigDecimal("1.0"); 
        
        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, weeklyRate);

        assertThat(result.getTargetIntakeKcal()).isEqualTo(1500);
    }

    @Test
    void calculateTargetIntake_SumsCurrentProgress_HandlesNullMacros() {
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(2500);
        when(reportDAO.calculateDailyWorkoutExpenditure(anyInt(), any())).thenReturn(300);
        
        FoodLog log1 = new FoodLog();
        log1.setCalories(500);
        log1.setProteinG(new BigDecimal("30"));
        log1.setFatsG(new BigDecimal("10"));
        log1.setCarbsG(new BigDecimal("50"));

        FoodLog log2 = new FoodLog();
        log2.setCalories(200);
        log2.setProteinG(null); 
        log2.setFatsG(null);
        log2.setCarbsG(null);

        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Arrays.asList(log1, log2));
        when(weightLogDAO.getAbsoluteLatestWeight(1)).thenReturn(null); 

        GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, BigDecimal.ZERO);

        assertThat(result.getCurrentIntakeKcal()).isEqualTo(700);
        assertThat(result.getCurrentProteinGrams()).isEqualTo(30);
        assertThat(result.getCurrentFatsGrams()).isEqualTo(10);
        assertThat(result.getCurrentCarbsGrams()).isEqualTo(50);
        assertThat(result.getCurrentWeightKg()).isEqualTo(100.0f); 
    }

    @Test
void calculateMacros_UsesCarbFloorLogic() {
    when(userDAO.selectUser(1)).thenReturn(testUser);
    when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(1200);
    when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

    GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, new BigDecimal("1.0"));

    assertThat(result.getCarbsGrams()).isEqualTo(Math.round(testUser.getStartWeightKg().floatValue()));
}

    @Test
void calculateTargetIntake_UserNotFound_ThrowsException() {
    when(userDAO.selectUser(999)).thenReturn(null);
    
    assertThatThrownBy(() -> goalService.calculateTargetIntakeByWeeklyRate(999, BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Пользователь не найден");
}
    @Test
void calculateMacros_ExtremelyLowCalories_RespectsCarbFloor() {
    testUser.setStartWeightKg(new BigDecimal("100"));
    when(userDAO.selectUser(1)).thenReturn(testUser);
    when(reportDAO.calculateDailyNeatExpenditure(1)).thenReturn(1000); 
    when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

    GoalResult result = goalService.calculateTargetIntakeByWeeklyRate(1, new BigDecimal("1.5"));

    assertThat(result.getCarbsGrams()).isEqualTo(100);
}
}