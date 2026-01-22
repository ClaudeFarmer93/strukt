import {Typography, Container, Box, Snackbar, Alert, Grid} from "@mui/material";
import {useAuth} from "../auth/useAuth";
import {useEffect, useState} from "react";
import type {Habit, UserHabit} from "../types/types.ts";
import UserStats from "../components/UserStats.tsx";
import HabitCard from "../components/HabitCard.tsx";
import {
    getRandomDailyHabit,
    getRandomWeeklyHabit
} from "../api/habitApi.ts";
import TodaysChallenges from "../components/TodaysChallenges.tsx";
import {acceptUserHabit, completeUserHabit, deleteUserHabit, getMyHabits} from "../api/userhabitApi.ts";

export default function DashboardPage() {
const {user} = useAuth();

const [dailyHabit, setDailyHabit] = useState<Habit |null>(null);
const [weeklyHabit, setWeeklyHabit] = useState<Habit| null>(null);
const [myHabits, setMyHabits] = useState<UserHabit[]>([]) ;
const [loadingDailyHabit, setLoadingDailyHabit] = useState<boolean>(true);
const [loadingWeeklyHabit, setLoadingWeeklyHabit] = useState<boolean>(true);
const [loadingMyHabits, setLoadingMyHabits] = useState<boolean>(true);
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

const fetchMyHabits = async () => {
    setLoadingMyHabits(true);
    try{
        const response = await getMyHabits();
        setMyHabits(response.data);
    } catch (err) {
        console.error("Failed to load my habits", err);
    } finally {
        setLoadingMyHabits(false);
    }
};

const handleAccept = async (habit: Habit |null) => {
    if(!habit) return;
    try {
        await acceptUserHabit(habit.id);
        setSnackbar({open: true, message: `"${habit.name}" added to your list!`, severity: "success"});
        fetchMyHabits();
        if(habit.frequency === "DAILY"){
            fetchDailyHabit();
        }else {
            fetchWeeklyHabit();
        }
    } catch (err){
        setSnackbar({open: true, message:"Failed to add habit. Maybe you're already tracking it?", severity: "error"});
        console.error(err)
    }
};

const handleComplete = async (habitId: string) => {
    try {
        await completeUserHabit(habitId);
        setSnackbar({open: true, message: "Habit completed! XP earned", severity: "success"});
        await fetchMyHabits();
    } catch (err) {
        setSnackbar({open: true, message: "Failed to complete this habit", severity: "error"});
        console.error("Failed to complete", err);
    }
};

const handleDelete = async (habitId:string) => {
    try {
        await deleteUserHabit(habitId);
        setSnackbar({open: true, message: "Habit deleted", severity : "success"});
        await fetchMyHabits();
    } catch (err) {
        setSnackbar({open: true, message: "Failed to delete habit", severity: "error"})
        console.error("Failed to complete", err);
    }
}

    useEffect(() => {
        fetchDailyHabit();
        fetchWeeklyHabit();
        fetchMyHabits();
    }, []);
if(!user) return null;

    return (

        <Container maxWidth ="lg">
            <Typography variant="h4" sx={{mb:3}}>
                Welcome back, {user.username}
            </Typography>
            <Grid container spacing={3}>
                <Grid size={{xs: 12, md:8}}>
                    <UserStats user={user} />

            <Typography variant="h5" sx={{mb:2}}>
                Today's suggestions
            </Typography>
            <Box sx={{display: "flex", flexWrap: "wrap", gap: 2}}>
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
            </Grid>
            <Grid size={{ xs: 12, md: 4}}>
                <TodaysChallenges habits={myHabits}
                                  onComplete={handleComplete}
                                  loading={loadingMyHabits}
                onDelete={handleDelete}/>

            </Grid>
            </Grid>
            <Snackbar
            open={snackbar.open}
            autoHideDuration={4000}
            onClose={() =>
                setSnackbar({...snackbar, open:false})}>
                <Alert severity={snackbar.severity} onClose={() => setSnackbar({...snackbar, open:false})}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
}