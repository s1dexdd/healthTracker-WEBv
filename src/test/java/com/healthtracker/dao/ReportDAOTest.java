package com.healthtracker.dao;

import com.healthtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportDAOTest {

    @Mock
    private FoodLogDAO foodLogDAO;
    @Mock
    private WorkoutLogDAO workoutLogDAO;
    @Mock
    private BMRDAO bmrdao;
    @Mock
    private UserDAO userDAO;
    @Mock
    private WeightLogDAO weightLogDAO;

    @InjectMocks
    private ReportDAO reportDAO;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setHeightCm(180);
        testUser.setAge(25);
        testUser.setGender(User.Gender.MALE);
        testUser.setActivityLevel(User.ActivityLevel.MID);
        testUser.setStartWeightKg(new BigDecimal("80.00"));
        testUser.setTargetWeightKg(new BigDecimal("75.00"));
    }

    @Test
    void calculateBMI_CorrectValue() {
        when(userDAO.selectUser(1)).thenReturn(testUser);
        when(weightLogDAO.getAbsoluteLatestWeight(1)).thenReturn(new BigDecimal("80.00"));

        BigDecimal bmi = reportDAO.calculateBMI(1);

        assertThat(bmi).isEqualByComparingTo("24.69");
    }

    @Test
    void getBMICategory_CorrectLabels() {
        assertThat(reportDAO.getBMICategory(new BigDecimal("18.0"))).isEqualTo("Недостаточный вес");
        assertThat(reportDAO.getBMICategory(new BigDecimal("22.0"))).isEqualTo("Нормальный вес");
        assertThat(reportDAO.getBMICategory(new BigDecimal("27.0"))).isEqualTo("Избыточный вес");
        assertThat(reportDAO.getBMICategory(new BigDecimal("32.0"))).isEqualTo("Ожирение I степени");
    }

    @Test
    void calculateDailyTotalExpenditure_SumsCorrect() {
        int userId = 1;
        Date today = new Date(System.currentTimeMillis());
        
        when(bmrdao.calculateBMR(userId)).thenReturn(2000);
        when(userDAO.selectUser(userId)).thenReturn(testUser);
        
        int total = reportDAO.calculateDailyTotalExpenditure(userId, today);

        int expected = (int) (2000 * User.ActivityLevel.MID.getCoefficient());
        assertThat(total).isEqualTo(expected);
    }
    @ParameterizedTest
@CsvSource({
    "18.0, Недостаточный вес",
    "22.0, Нормальный вес",
    "27.0, Избыточный вес",
    "32.0, Ожирение I степени",
    "37.0, Ожирение II степени",
    "42.0, Ожирение III степени"
})
void getBMICategory_CheckAllRanges(String bmiValue, String expectedCategory) {
    assertThat(reportDAO.getBMICategory(new BigDecimal(bmiValue))).isEqualTo(expectedCategory);
}
}