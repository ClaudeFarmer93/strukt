import {
    Typography,
    Container,
    Box,
    IconButton,
    Paper,
    Chip,
    Stack,
    CircularProgress
} from "@mui/material";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import {useAuth} from "../auth/useAuth.ts";
import {useEffect, useState} from "react";
import type {HabitCompletion} from "../types/types.ts";
import {getWeekCompletions} from "../api/userhabitApi.ts";
import {
    startOfWeek,
    format,
    eachDayOfInterval,
    addDays,
    subWeeks,
    addWeeks,
    isSameDay,
    isAfter,
    isToday as dateFnsIsToday
} from "date-fns";

const DAYS :string[] = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];

const difficultyColor = {
    EASY: "success",
    MEDIUM: "warning",
    HARD: "error"
} as const;

const getMonday = (date: Date): Date =>
    startOfWeek(date, {weekStartsOn: 1});

const formatDate = (date: Date): string =>
    format(date, "yyyy-MM-dd");

const formatDateDisplay = (date: Date): string =>
    format(date, "MMM d");

const getWeekDates = (monday: Date): Date[] =>
    eachDayOfInterval({start: monday, end: addDays(monday, 6)});

const isCurrentWeek = (monday: Date): boolean =>
    isSameDay(monday, getMonday(new Date()));

const isFutureWeek = (monday: Date): boolean =>
    isAfter(monday, getMonday(new Date()));

export default function CalendarPage() {
    const {user} = useAuth();
    const [completions, setCompletions] = useState<HabitCompletion[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [currentMonday, setCurrentMonday] = useState<Date>(() => getMonday(new Date()));

    const fetchCompletions = async (monday: Date) => {
        setLoading(true);
        try {
            const res = await getWeekCompletions(formatDate(monday));
            setCompletions(res.data);
        } catch {
            setCompletions([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCompletions(currentMonday);
    }, [currentMonday]);

    const goToPreviousWeek = () => {
        setCurrentMonday(subWeeks(currentMonday, 1));
    };

    const goToNextWeek = () => {
        if (isFutureWeek(currentMonday)) return;
        setCurrentMonday(addWeeks(currentMonday, 1));
    };

    const weekDates = getWeekDates(currentMonday);

    const getCompletionsForDay = (date: Date): HabitCompletion[] => {
        const dateStr = formatDate(date);
        return completions.filter(c => c.completionDate === dateStr);
    };

    const isToday = (date: Date): boolean => dateFnsIsToday(date);

    if (!user) return null;

    const weekRange = `${formatDateDisplay(weekDates[0])} - ${formatDateDisplay(weekDates[6])}`;

    return (
        <Container maxWidth="lg">
            <Typography variant="h4" sx={{mb: 3}}>
                My Week in Habits
            </Typography>

            <Box sx={{display: "flex", alignItems: "center", justifyContent: "center", mb: 3, gap: 2}}>
                <IconButton onClick={goToPreviousWeek} size="large">
                    <ChevronLeftIcon/>
                </IconButton>
                <Box sx={{textAlign: "center", minWidth: 200}}>
                    <Typography variant="h6">
                        {weekRange}
                    </Typography>
                    {isCurrentWeek(currentMonday) && (
                        <Chip label="Current Week" size="small" color="primary" sx={{mt: 0.5}}/>
                    )}
                </Box>
                <IconButton
                    onClick={goToNextWeek}
                    size="large"
                    disabled={isCurrentWeek(currentMonday)}
                >
                    <ChevronRightIcon/>
                </IconButton>
            </Box>

            {loading ? (
                <Box sx={{display: "flex", justifyContent: "center", py: 4}}>
                    <CircularProgress/>
                </Box>
            ) : (
                <Box
                    sx={{
                        display: "grid",
                        gridTemplateColumns: "repeat(7, 1fr)",
                        gap: 1,
                    }}
                >
                    {weekDates.map((date, index) => {
                        const dayCompletions = getCompletionsForDay(date);
                        const hasCompletions = dayCompletions.length > 0;
                        const today = isToday(date);

                        return (
                            <Paper
                                key={index}
                                elevation={today ? 4 : 1}
                                sx={{
                                    p: 1.5,
                                    minHeight: 180,
                                    bgcolor: "background.paper",
                                    border: today ? "2px solid" : "1px solid",
                                    borderColor: today ? "primary.main" : "divider",
                                    borderRadius: 2,
                                    display: "flex",
                                    flexDirection: "column",
                                }}
                            >
                                <Box sx={{textAlign: "center", mb: 1}}>
                                    <Typography
                                        variant="caption"
                                        sx={{
                                            fontWeight: today ? 700 : 500,
                                            color: today ? "primary.main" : "text.secondary"
                                        }}
                                    >
                                        {DAYS[index]}
                                    </Typography>
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            fontWeight: today ? 700 : 400,
                                            color: today ? "primary.main" : "text.primary"
                                        }}
                                    >
                                        {date.getDate()}
                                    </Typography>
                                </Box>

                                <Box sx={{flex: 1, overflowY: "auto"}}>
                                    {hasCompletions ? (
                                        <Stack spacing={0.5}>
                                            {dayCompletions.map((completion) => (
                                                <Box
                                                    key={completion.id}
                                                    sx={{
                                                        p: 0.75,
                                                        bgcolor: "action.hover",
                                                        borderRadius: 1,
                                                        display: "flex",
                                                        alignItems: "flex-start",
                                                        gap: 0.5,
                                                    }}
                                                >
                                                    <CheckCircleIcon
                                                        sx={{
                                                            fontSize: 14,
                                                            color: "success.main",
                                                            mt: 0.25
                                                        }}
                                                    />
                                                    <Box sx={{flex: 1, minWidth: 0}}>
                                                        <Typography
                                                            variant="caption"
                                                            sx={{
                                                                display: "block",
                                                                fontWeight: 500,
                                                                lineHeight: 1.2,
                                                                wordBreak: "break-word"
                                                            }}
                                                        >
                                                            {completion.habitName}
                                                        </Typography>
                                                        <Box sx={{display: "flex", gap: 0.5, mt: 0.25, flexWrap: "wrap"}}>
                                                            <Chip
                                                                label={completion.difficulty}
                                                                color={difficultyColor[completion.difficulty]}
                                                                size="small"
                                                                sx={{height: 14, fontSize: "0.6rem"}}
                                                            />
                                                            <Typography variant="caption" color="text.secondary">
                                                                +{completion.xpEarned}XP
                                                            </Typography>
                                                        </Box>
                                                    </Box>
                                                </Box>
                                            ))}
                                        </Stack>
                                    ) : (
                                        <Typography
                                            variant="caption"
                                            color="text.disabled"
                                            sx={{display: "block", textAlign: "center", mt: 2}}
                                        >
                                            No completions
                                        </Typography>
                                    )}
                                </Box>

                                {hasCompletions && (
                                    <Box sx={{textAlign: "center", mt: 1, pt: 0.5, borderTop: "1px solid", borderColor: "divider"}}>
                                        <Typography variant="caption" color="text.secondary">
                                            {dayCompletions.reduce((sum, c) => sum + c.xpEarned, 0)} XP
                                        </Typography>
                                    </Box>
                                )}
                            </Paper>
                        );
                    })}
                </Box>
            )}

            <Box sx={{mt: 3, textAlign: "center"}}>
                <Typography variant="body2" color="text.secondary">
                    Total XP this week: {completions.reduce((sum, c) => sum + c.xpEarned, 0)} XP
                </Typography>
                <Typography variant="caption" color="text.disabled">
                    {completions.length} habit{completions.length !== 1 ? "s" : ""} completed
                </Typography>
            </Box>
        </Container>
    );
}