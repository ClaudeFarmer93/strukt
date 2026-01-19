import {Card, CardContent, Typography, Box, Chip, IconButton, LinearProgress} from "@mui/material";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import type {UserHabit} from "../types/types.ts";

interface TodaysChallengesProps {
    habits : UserHabit[];
    onComplete: (habitId: string) => void;
    loading: boolean;
}

const difficultyColor ={
    EASY: "success",
    MEDIUM: "warning",
    HARD: "error"
} as const;

export default function TodaysChallenges({habits, onComplete, loading}: TodaysChallengesProps) {

    const isCompletedToday = (habit: UserHabit): boolean => {
        if(!habit.lastCompletedDate) return false;
        const today = new Date().toISOString().split("T")[0];
        return habit.lastCompletedDate === today;
    };

    const isCompletedThisWeek = (habit: UserHabit) : boolean => {
        if(!habit.lastCompletedDate) return false;
        const lastCompleted = new Date(habit.lastCompletedDate);
        const today = new Date();
        const startOfWeek = new Date(today);
        startOfWeek.setDate(today.getDate() - today.getDay());
        startOfWeek.setHours(0,0,0,0);
        return lastCompleted >= startOfWeek;
    }

    const isCompleted = (habit: UserHabit): boolean => {
        return habit.frequency === "DAILY" ? isCompletedToday(habit) : isCompletedThisWeek(habit);
    };

    const dailyHabits = habits.filter(h => h.frequency === "DAILY");
    const weeklyHabits = habits.filter(h => h.frequency === "WEEKLY");

    const completedDaily = dailyHabits.filter(isCompletedToday).length;
    const completedWeekly = weeklyHabits.filter(isCompletedThisWeek).length;


    return (
        <Card sx={{ minWidth: 300, height: "100%"}}>
            <CardContent>
                <Typography variant="h6" gutterBottom>
                    🗒️Today's Habits
                </Typography>

                {loading ? (
                    <Typography>Loading...</Typography>
                ): habits.length === 0 ? <Typography color="textSecondary">
                    No habits yet. Accept some of the daily or weekly challenges!
                </Typography> : (
                    <>
                        {dailyHabits.length > 0 && (
                            <Box sx={{ mb:2 }}>
                                <Typography variant={"body2"} color={"textSecondary"}>
                                    Daily: {completedDaily}/{dailyHabits.length}
                                </Typography>
                                <LinearProgress
                                    variant={"determinate"}
                                    value={(completedDaily/dailyHabits.length)* 100}
                                    sx={{height: 6, borderRadius: 3, mb: 1}}/>
                            </Box>
                        )}
                        {weeklyHabits.length > 0 && (
                            <Box sx={{ mb:2 }}>
                    <Typography variant={"body2"} color={"textSecondary"}>
                        Weekly: {completedWeekly}/{weeklyHabits.length}
                    </Typography>
                    <LinearProgress
                        variant={"determinate"}
                        value={(completedWeekly/weeklyHabits.length)* 100}
                        sx={{height: 6, borderRadius: 3, mb: 1}}/>
                    </Box>
                        )}
                        <Box sx={{ display: "flex", flexDirection: "column", gap: 1}}>
                            {habits.map((habit) => {
                            const completed = isCompleted(habit);
                               return (
                                 <Box
                                 key ={habit.id}
                                 sx={{
                                     display: "flex",
                                     alignItems: "center",
                                     justifyContent: "space-between",
                                     p:1,
                                     borderRadius: 2,
                                     bgcolor: completed ? "action.selected" : "background.paper",
                                     border: "1px solid",
                                     borderColor: "divider",
                                     opacity: completed ? 0.7 : 1,
                                 }}
                                 >
                                     <Box sx={{
                                         display: "flex", alignItems: "center", gap:1
                                     }}>
                                         <IconButton size={"small"} onClick={() => !completed && onComplete(habit.habitId)}
                                                     disabled={completed}
                                                     color={completed ? "success" : "default"}
                                                     >
                                             {completed ? <CheckCircleIcon /> : <CheckCircleOutlineIcon />}
                                         </IconButton>
                                     <Typography variant="body2" sx={{
                                         textDecoration: completed ? "line-through" : "none",
                                     }}
                                                 >
                                         {habit.habitName}
                                     </Typography>
                                     </Box>
                                     <Box sx={{ display: "flex", gap: 0.5}}>
                                         <Chip label={habit.difficulty}
                                               color={difficultyColor[habit.difficulty]}
                                               size={"small"}
                                               sx={{height: 18, fontSize: "0.65rem"}}
                                               />
                                         {habit.currentStreak > 0 && (
                                             <Chip
                                                 label={`🔥 ${habit.currentStreak}`}
                                                 size={"small"}
                                                 sx={{height: 18, fontSize: "0.65rem"}}
                                                 />
                                         )}
                                         <Typography variant="caption" color="textSecondary">
                                         +{habit.xp} XP
                                     </Typography>
                                     </Box>
                                 </Box>
                                );
                            })}
                        </Box>
                    </>
                )}
            </CardContent>
        </Card>

    )
}