import {Typography, Container, Box, Snackbar, Alert} from "@mui/material";
import {useAuth} from "../auth/auth.tsx";
import {useEffect, useState} from "react";
import type {Habit} from "../types/types.ts";
import UserStats from "../components/UserStats.tsx";
import HabitCard from "../components/HabitCard.tsx";
import {acceptUserHabit, getRandomDailyHabit, getRandomWeeklyHabit} from "../api/habitApi.ts";

export default function DashboardPage() {
const {user} = useAuth();

const [dailyHabit, setDailyHabit] = useState<Habit |null>(null);
const [weeklyHabit, setWeeklyHabit] = useState<Habit| null>(null);
const [loadingDailyHabit, setLoadingDailyHabit] = useState<boolean>(true);
const [loadingWeeklyHabit, setLoadingWeeklyHabit] = useState<boolean>(true);
const [snackbar, setSnackbar] = useState<{open: boolean; message: string;
    severity:"success" |"error"}> ({open: false, message: "", severity: "success"});

const fetchDailyHabit = async () => {
    setLoadingDailyHabit(true);
    try {
        const response = await getRandomDailyHabit();
        setDailyHabit(response.data);
    } catch (err) {
        console.error("Failed to load daily habit", err);
    } finally {
        setLoadingDailyHabit(false);
    }
};

const fetchWeeklyHabit = async () => {
    setLoadingWeeklyHabit(true);
    try {
        const response = await getRandomWeeklyHabit();
        setWeeklyHabit(response.data);
    } catch (err) {
        console.error("Failed to fetch weekly habit", err);
    } finally {
        setLoadingWeeklyHabit(false);
    }
};

const handleAccept = async (habit: Habit |null) => {
    if(!habit) return;
    try {
        await acceptUserHabit(habit.id);
        setSnackbar({open: true, message: `"${habit.name}" added to your list!`, severity: "success"});
        if(habit.frequency === "DAILY"){

            fetchDailyHabit();
        }else {
            fetchWeeklyHabit();
        }
    } catch (err){
        setSnackbar({open: true, message:"Failed to add habit. Maybe you're already tracking it?", severity: "error"});
        console.error(err)
    }
}

    useEffect(() => {
        fetchDailyHabit();
        fetchWeeklyHabit();
    }, []);
if(!user) return null;

    return (

        <Container maxWidth ="md">
            <Typography variant="h4" sx={{mb:3}}>
                Welcome back, {user.username}
            </Typography>
            <Typography variant="h5" sx={{mb:2}}>
                Today's suggestions
            </Typography>
            <Box sx={{display: "flex", flexWrap: "wrap", justifyContent: "center"}}>
                <HabitCard habit={dailyHabit}
                           title={"Daily Habit"}
                           onAccept={() => handleAccept(dailyHabit)}
                           onReroll={fetchDailyHabit}
                           loading={loadingDailyHabit}/>
                <HabitCard habit={weeklyHabit}
                           title={"Weekly Habit"}
                           onAccept={() => handleAccept(weeklyHabit)}
                           onReroll={fetchWeeklyHabit}
                           loading={loadingWeeklyHabit}/>
            </Box>
            <Snackbar
            open={snackbar.open}
            autoHideDuration={4000}
            onClose={() =>
                setSnackbar({...snackbar, open:false})}>
                <Alert severity={snackbar.severity} onClose={() => setSnackbar({...snackbar, open:false})}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
            <UserStats user={user} />
        </Container>

    )
}