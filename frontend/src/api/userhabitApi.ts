import axios from "axios";
import type {UserHabit} from "../types/types.ts";


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