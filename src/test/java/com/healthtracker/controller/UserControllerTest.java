package com.healthtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtracker.dao.*;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;
import com.healthtracker.service.GoalCalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private GoalCalculationService goalService;

    @MockBean
    private WeightLogDAO weightLogDAO;

    @MockBean
    private FoodLogDAO foodLogDAO;

    @MockBean
    private WorkoutLogDAO workoutLogDAO;

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("pass");

        when(userDAO.getUserByEmail("test@test.com")).thenReturn(user);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void testLoginFail() throws Exception {
        User user = new User();
        user.setEmail("wrong@test.com");
        user.setPassword("pass");

        when(userDAO.getUserByEmail(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setName("New User");
        user.setEmail("new@test.com");
        
        when(userDAO.insertUser(any(User.class))).thenReturn(1);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testCalculateGoal() throws Exception {
        GoalResult goalResult = new GoalResult(2000, 500, "Loss", 0.5f, 150, 70, 200);
        
        when(goalService.calculateTargetIntakeByWeeklyRate(anyInt(), any(BigDecimal.class)))
                .thenReturn(goalResult);

        mockMvc.perform(get("/api/users/1/calculate-goal")
                .param("weeklyRate", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetIntakeKcal").value(2000));
    }

    @Test
    void testGetFoodLogs() throws Exception {
        when(foodLogDAO.getFoodLogsByDate(anyInt(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/food-logs")
                .param("userId", "1")
                .param("date", "2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testDeleteFoodSuccess() throws Exception {
        when(foodLogDAO.deleteFoodLog(100)).thenReturn(true);

        mockMvc.perform(delete("/api/users/food/100"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFoodNotFound() throws Exception {
        when(foodLogDAO.deleteFoodLog(999)).thenReturn(false);

        mockMvc.perform(delete("/api/users/food/999"))
                .andExpect(status().isNotFound());
    }
}