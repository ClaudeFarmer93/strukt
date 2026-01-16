package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Habit;
import org.example.backend.repository.HabitRepository;
import org.example.backend.service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }
    @GetMapping("/daily")
    public ResponseEntity<Habit> getRandomDailyHabit() {
        return ResponseEntity.ok(habitService.getRandomDailyHabit());
    }

    @GetMapping("/weekly")
    public ResponseEntity<Habit> getRandomWeeklyHabit() {
        return ResponseEntity.ok(habitService.getRandomWeeklyHabit());
    }
}
