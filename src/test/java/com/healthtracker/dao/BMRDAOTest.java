package com.healthtracker.dao;

import com.healthtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BMRDAOTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private WeightLogDAO weightLogDAO;

    @InjectMocks
    private BMRDAO bmrdao;

    private User maleUser;
    private User femaleUser;

    @BeforeEach
    void setUp() {
        maleUser = new User();
        maleUser.setUserId(1);
        maleUser.setGender(User.Gender.MALE);
        maleUser.setHeightCm(180);
        maleUser.setAge(25);
        maleUser.setStartWeightKg(new BigDecimal("80"));

        femaleUser = new User();
        femaleUser.setUserId(2);
        femaleUser.setGender(User.Gender.FEMALE);
        femaleUser.setHeightCm(165);
        femaleUser.setAge(30);
        femaleUser.setStartWeightKg(new BigDecimal("60"));
    }

    @Test
    void calculateBMR_Male_Correct() {
        when(userDAO.selectUser(1)).thenReturn(maleUser);
        when(weightLogDAO.getAbsoluteLatestWeight(1)).thenReturn(new BigDecimal("80"));

        int bmr = bmrdao.calculateBMR(1);

        int expected = (int) Math.round((10 * 80) + (6.25 * 180) - (5 * 25) + 5);
        assertThat(bmr).isEqualTo(expected);
    }

    @Test
    void calculateBMR_Female_Correct() {
        when(userDAO.selectUser(2)).thenReturn(femaleUser);
        when(weightLogDAO.getAbsoluteLatestWeight(2)).thenReturn(new BigDecimal("60"));

        int bmr = bmrdao.calculateBMR(2);

        int expected = (int) Math.round((10 * 60) + (6.25 * 165) - (5 * 30) - 161);
        assertThat(bmr).isEqualTo(expected);
    }

    @Test
    void calculateBMR_UserNotFound_ReturnsZero() {
        when(userDAO.selectUser(99)).thenReturn(null);
        assertThat(bmrdao.calculateBMR(99)).isEqualTo(0);
    }
}