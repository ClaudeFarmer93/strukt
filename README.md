# Strukt

A gamified habit tracking application that helps users build and maintain positive habits through XP rewards, level progression, and streak tracking.

## Overview

Strukt is a full-stack web application designed to make habit building engaging and rewarding. Users receive daily and weekly habit suggestions from a curated library, can adopt habits to track, and earn experience points for completing them. The gamification system encourages consistency through streak tracking and level progression.

## Features

### Habit Management
- **Habit Library**: Browse a curated collection of habits categorized by difficulty and frequency
- **Daily/Weekly Suggestions**: Receive randomized habit recommendations tailored to your needs
- **Accept & Track**: Add habits to your personal dashboard and track progress
- **Weekly Calendar View**: Visualize your completed habits across the week

### Gamification System
- **Experience Points (XP)**: Earn XP based on habit difficulty
  - Easy: 25 XP
  - Medium: 50 XP
  - Hard: 100 XP
- **Level Progression**: Accumulate XP to level up (each level requires `100 × level` XP)
- **Streak Tracking**: Maintain consecutive completion streaks for individual habits and overall activity
- **User Statistics**: View your total XP, current level, and streak records

### Authentication
- Secure login via GitHub OAuth2
- Automatic user profile creation on first login

## Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.0 (Java 21)
- **Database**: MongoDB
- **Authentication**: Spring Security OAuth2 (GitHub)
- **Build Tool**: Maven

### Frontend
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **UI Library**: Material-UI (MUI)
- **Routing**: React Router
- **HTTP Client**: Axios

## Project Structure

```
strukt/
├── backend/
│   └── src/main/java/org/example/backend/
│       ├── controller/     # REST API endpoints
│       ├── service/        # Business logic
│       ├── repository/     # MongoDB data access
│       ├── model/          # Entity classes
│       ├── security/       # OAuth2 configuration
│       └── exception/      # Custom exceptions
│
└── frontend/
    └── src/
        ├── pages/          # Page components
        ├── components/     # Reusable UI components
        ├── auth/           # Authentication context
        ├── api/            # API client functions
        └── types/          # TypeScript interfaces
```

## API Endpoints

### Public
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/habits` | List all available habits |
| GET | `/api/habits/daily` | Get a random daily habit suggestion |
| GET | `/api/habits/weekly` | Get a random weekly habit suggestion |

### Protected (requires authentication)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/me` | Get current user info |
| GET | `/api/my-habits` | List user's active habits |
| POST | `/api/my-habits/{habitId}` | Adopt a habit |
| DELETE | `/api/my-habits/{habitId}` | Remove a habit |
| POST | `/api/my-habits/{habitId}/complete` | Mark habit as completed |
| GET | `/api/completions/week` | Get completions for the week |

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- MongoDB
- GitHub OAuth App credentials

### Environment Variables

Create the following environment variables:

```
GITHUB_ID=your_github_client_id
GITHUB_SECRET=your_github_client_secret
MONGODB_URI=your_mongodb_connection_string
```

### Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`.

### Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`.

## Data Models

### AppUser
User profile with stats including total XP, level, and streak information.

### Habit
Pre-defined habit templates with name, description, category, difficulty, and frequency.

### UserHabit
Links users to habits they're tracking, with individual progress stats.

### HabitCompletion
Records of completed habits used for calendar view and statistics.

## How It Works

1. **Sign in** with your GitHub account
2. **Browse** daily and weekly habit suggestions on your dashboard
3. **Accept** habits you want to track
4. **Complete** habits to earn XP and maintain your streak
5. **Level up** as you accumulate experience points
6. **View** your weekly progress in the calendar view

## License

This project is for educational purposes.