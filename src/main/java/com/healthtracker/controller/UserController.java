package com.healthtracker.controller;

import com.healthtracker.dao.*;
import com.healthtracker.model.*;
import com.healthtracker.service.GoalCalculationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserDAO userDAO;
    private final GoalCalculationService goalService;
    private final WeightLogDAO weightLogDAO;
    private final FoodLogDAO foodLogDAO;
    private final WorkoutLogDAO workoutLogDAO;

    public UserController(UserDAO userDAO, GoalCalculationService goalService, WeightLogDAO weightLogDAO, FoodLogDAO foodLogDAO, WorkoutLogDAO workoutLogDAO) {
        this.userDAO = userDAO;
        this.goalService = goalService;
        this.weightLogDAO = weightLogDAO;
        this.foodLogDAO = foodLogDAO;
        this.workoutLogDAO = workoutLogDAO;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginData) {
        User user = userDAO.getUserByEmail(loginData.getEmail());
        if (user != null && user.getPassword() != null && user.getPassword().equals(loginData.getPassword())) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if (userDAO.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        int id = userDAO.insertUser(user);
        user.setUserId(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(@RequestBody User user) {
        boolean updated = userDAO.updateUserAllSettings(
            user.getUserId(),
            user.getName(),
            user.getHeightCm(),
            user.getStartWeightKg(),
            user.getTargetWeightKg(),
            user.getAge(),
            user.getGender(),
            user.getActivityLevel(),
            user.getWeeklyGoalKg()
        );
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/calculate-goal")
    public GoalResult calculateGoal(@PathVariable int id, @RequestParam BigDecimal weeklyRate) {
        return goalService.calculateTargetIntakeByWeeklyRate(id, weeklyRate);
    }

    @PostMapping("/food")
    public void addFood(@RequestBody FoodLog food) {
        food.setLogDate(new java.sql.Timestamp(System.currentTimeMillis()));
        foodLogDAO.insertFoodLog(food);
    }

    @PostMapping("/workout")
    public void addWorkout(@RequestBody WorkoutLog workout) {
        workout.setLogDate(new java.sql.Timestamp(System.currentTimeMillis()));
        workoutLogDAO.insertWorkoutLog(workout);
    }

    @GetMapping("/food-logs")
    public List<FoodLog> getFoodLogs(@RequestParam int userId, @RequestParam String date) {
        return foodLogDAO.getFoodLogsByDate(userId, java.sql.Date.valueOf(date));
    }

    @GetMapping("/workout-logs")
    public List<WorkoutLog> getWorkoutLogs(@RequestParam int userId, @RequestParam String date) {
        return workoutLogDAO.getWorkoutLogsByDate(userId, java.sql.Date.valueOf(date));
    }

    @PostMapping("/weight")
    public void addWeight(@RequestBody WeightLog weightLog) {
        if (weightLog.getLogDate() == null) {
            weightLog.setLogDate(new java.sql.Date(System.currentTimeMillis()));
        }
        weightLogDAO.insertWeightLog(weightLog);
    }

    @DeleteMapping("/food/{logId}")
    public ResponseEntity<Void> deleteFood(@PathVariable int logId) {
        boolean deleted = foodLogDAO.deleteFoodLog(logId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}