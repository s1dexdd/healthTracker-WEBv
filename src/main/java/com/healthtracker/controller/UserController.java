package com.healthtracker.controller;

import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.dao.WorkoutLogDAO;
import com.healthtracker.model.FoodLog;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import com.healthtracker.model.WorkoutLog;
import com.healthtracker.service.GoalCalculationService;

import java.util.List;
import java.util.Map;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserDAO userDAO;
    private final GoalCalculationService goalService; // Добавляем сервис
    private final WeightLogDAO weightLogDAO;
    private final FoodLogDAO foodLogDAO;
    private final WorkoutLogDAO workoutLogDAO;

    // Обновите конструктор, чтобы Spring вставил оба бина
    public UserController(UserDAO userDAO, GoalCalculationService goalService, WeightLogDAO weightLogDAO,FoodLogDAO foodLogDAO,WorkoutLogDAO workoutLogDAO) {
    this.userDAO = userDAO;
    this.goalService = goalService;
    this.weightLogDAO = weightLogDAO;
    this.foodLogDAO =foodLogDAO;
    this.workoutLogDAO=workoutLogDAO;
}

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userDAO.selectUser(id);
    }

    // НОВЫЙ ЭНДПОИНТ: именно его сейчас ищет Angular
    @GetMapping("/{id}/calculate-goal")
    public GoalResult calculateGoal(@PathVariable int id, @RequestParam BigDecimal weeklyRate) {
        // weeklyRate придет из Angular (например, 0.5)
        return goalService.calculateTargetIntakeByWeeklyRate(id, weeklyRate);
    }
    @PostMapping("/weight")
public void addWeight(@RequestBody Map<String, Object> payload) {
    int userId = (int) payload.get("userId");
    // Преобразуем число в BigDecimal
    BigDecimal weight = new BigDecimal(payload.get("currentWeightKg").toString());
    
    WeightLog log = new WeightLog(userId, new java.sql.Date(System.currentTimeMillis()), weight);
    weightLogDAO.insertWeightLog(log);
}

    @PostMapping
    public void saveUser(@RequestBody User user) {
        if (user.getUserId() > 0) {
            userDAO.updateUserAllSettings(
                user.getUserId(), user.getName(), user.getHeightCm(),
                user.getStartWeightKg(), user.getTargetWeightKg(),
                user.getAge(), user.getGender(), user.getActivityLevel()
            );
        } else {
            userDAO.insertUser(user);
        }
    }
    @PostMapping("/food")
public void addFood(@RequestBody FoodLog food) {
    // В бэкенде Timestamp, поэтому преобразуем дату
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
    // Преобразуем String в java.sql.Date
    java.sql.Date sqlDate = java.sql.Date.valueOf(date); 
    return foodLogDAO.getFoodLogsByDate(userId, sqlDate);
}

@GetMapping("/workout-logs")
public List<WorkoutLog> getWorkoutLogs(@RequestParam int userId, @RequestParam String date) {
    // Аналогично для тренировок
    java.sql.Date sqlDate = java.sql.Date.valueOf(date);
    return workoutLogDAO.getWorkoutLogsByDate(userId, sqlDate);
}
}