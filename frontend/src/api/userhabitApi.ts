import axios from "axios";
import type {UserHabit, HabitCompletion} from "../types/types.ts";


const api = axios.create({
    baseURL: "/api",
    withCredentials: true,
});

export const getMyHabits =() =>
    api.get<UserHabit[]>("/my-habits");

export const acceptUserHabit=(habitId: string) =>
    api.post<UserHabit>(`/my-habits/${habitId}`);

export const deleteUserHabit = (habitId: string) =>
    api.delete<void>(`/my-habits/${habitId}`);

export const completeUserHabit = (habitId: string) =>
    api.post<UserHabit>(`/my-habits/${habitId}/complete`);

export const getWeekCompletions = (date? : string) =>
    api.get<HabitCompletion[]>("/completions/week", {
        params: date ? { date } : {}
    });