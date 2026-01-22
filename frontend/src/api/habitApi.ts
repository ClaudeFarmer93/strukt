import axios from "axios";
import type {Habit} from "../types/types.ts";


const api = axios.create({
    baseURL: "/api",
    withCredentials: true,
});

export const getRandomDailyHabit = () =>
    api.get<Habit>("/habits/daily");

export const getRandomWeeklyHabit = () =>
    api.get<Habit>("/habits/weekly");

export const getAllHabits =() =>
    api.get<Habit[]>("/habits");



