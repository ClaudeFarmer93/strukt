import type {User} from "../types/types.ts";
import {Paper,Box, Typography, LinearProgress} from "@mui/material"
interface UserStatsProps {
    user: User;
}

export default function UserStats({user}: UserStatsProps) {
    const xpForNextLevel: number = user.level * 100;
    const currentLevelXp: number= user.totalXp % xpForNextLevel || 0;
    const progress: number = xpForNextLevel > 0 ? ( currentLevelXp / xpForNextLevel) * 100 : 0;

    return (
        <Paper sx={{ p:3, mb:3}}>
            <Box sx={{display: "flex",  justifyContent: "space-between", alignItems: "center", mb: 2}}>
                <Typography variant="h5">Level {user.level}</Typography>
                <Typography variant="body1">🔥{user.currentStreak} day streak</Typography>
            </Box>

            <Box sx={{ mb:1}}>
                <Typography variant="body2" color="textSecondary">
                    {currentLevelXp} / {xpForNextLevel} XP
                </Typography>
                <LinearProgress variant="determinate" value={progress} sx={{height: 10, borderRadius: 5}}/>
            </Box>
        </Paper>
    );
}