import {Card, CardContent, Typography, Box, Chip, Button} from "@mui/material";

import type {Habit} from "../types/types.ts";

interface HabitCardProps {
    habit: Habit  | null;
    title: string;
    onAccept: () => void;
    onReroll: () => void;
    loading: boolean;
}

 const difficultyColor = {
    EASY: "success",
    MEDIUM: "warning",
    HARD: "error",
} as const;

export default function HabitCard({habit, title, onAccept, onReroll, loading}: HabitCardProps) {
    return (
        <Card sx={{mindWidth: 300, maxWidth: 400, m:2}}>
            <CardContent>
                <Typography variant="h6" gutterBottom>
                    {title}
                </Typography>
                {loading ? (
                    <Typography>Loading...</Typography>
                ): habit ? (
                    <>
                        <Typography variant="h5" sx={{ mb:1}}>
                            {habit.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {habit.description}
                        </Typography>

                        <Box sx={{display: "flex", gap: 1, mb:2}}>
                            <Chip
                            label={habit.difficulty}
                            color={difficultyColor[habit.difficulty]}
                            size="small"/>
                            <Chip label={habit.category} variant="outlined"
                            size="small"/>
                            <Chip label={`${habit.xp} XP`} size="small"/>
                        </Box>

                        <Box sx={{display: "flex", gap:1}}>
                            <Button variant="contained" color="primary" onClick={onAccept}>
                                Accept
                            </Button>
                            <Button variant="outlined" color="secondary" onClick={onReroll}>
                                Reroll
                            </Button>
                        </Box>
                    </>
                ): (
                    <Typography variant="h5">No habit found.</Typography>
                )}
            </CardContent>
        </Card>
    );
}