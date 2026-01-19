export interface User {
    id: string;
    githubId: string;
    username: string;
    email: string;
    avatarUrl: string;
    totalXp: number;
    level: number;
    currentStreak: number;
    longestStreak: number;
    lastActiveDate: string | null;
}

export interface Habit {
    id: string;
    name: string;
    description: string;
    category: string;
    difficulty: "EASY" | "MEDIUM" | "HARD";
    frequency: "DAILY" | "WEEKLY";
    xp: number;
}

export interface UserHabit {
    id: string;
    userId: string;
    habitId: string;
    habitName: string;
    difficulty: "EASY" | "MEDIUM" | "HARD";
    frequency: "DAILY" | "WEEKLY";
    active: boolean;
    currentStreak: number;
    longestStreak: number;
    lastCompletedDate: string | null;
    totalCompletions: number;
    totalXpEarned: number;
}