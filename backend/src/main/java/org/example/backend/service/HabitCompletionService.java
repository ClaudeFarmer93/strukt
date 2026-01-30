package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.HabitCompletion;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.HabitCompletionRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitCompletionService {

    private final HabitCompletionRepository habitCompletionRepository;


    public HabitCompletion recordCompletion (String userId, UserHabit userHabit) {
        HabitCompletion completion = new HabitCompletion(userId, userHabit);
        return habitCompletionRepository.save(completion);
    }


    public List<HabitCompletion> getCompletionsForWeek(String userId, LocalDate dateInWeek) {
        LocalDate startOfWeek = getStartOfWeek(dateInWeek);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        String startDate = startOfWeek.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = endOfWeek.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return habitCompletionRepository.findByUserIdAndWeek(userId, startDate, endDate);
    }

    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

}

